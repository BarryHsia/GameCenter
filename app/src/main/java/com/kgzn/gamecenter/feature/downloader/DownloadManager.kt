package com.kgzn.gamecenter.feature.downloader

import android.util.Log
import com.kgzn.gamecenter.feature.downloader.connection.DownloaderClient
import com.kgzn.gamecenter.feature.downloader.db.IDownloadListDb
import com.kgzn.gamecenter.feature.downloader.db.IDownloadPartListDb
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItem
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadItemContext
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadJob
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadJobStatus
import com.kgzn.gamecenter.feature.downloader.downloaditem.DownloadStatus
import com.kgzn.gamecenter.feature.downloader.downloaditem.EmptyContext
import com.kgzn.gamecenter.feature.downloader.downloaditem.contexts.DuplicateRemoval
import com.kgzn.gamecenter.feature.downloader.downloaditem.contexts.RemovedBy
import com.kgzn.gamecenter.feature.downloader.part.Part
import com.kgzn.gamecenter.feature.downloader.utils.DuplicateFilterByPath
import com.kgzn.gamecenter.feature.downloader.utils.EmptyFileCreator
import com.kgzn.gamecenter.feature.downloader.utils.FileNameUtil
import com.kgzn.gamecenter.feature.downloader.utils.FileNameValidator
import com.kgzn.gamecenter.feature.downloader.utils.IDiskStat
import com.kgzn.gamecenter.feature.downloader.utils.OnDuplicateStrategy
import com.kgzn.gamecenter.feature.downloader.utils.OnDuplicateStrategy.Abort
import com.kgzn.gamecenter.feature.downloader.utils.OnDuplicateStrategy.AddNumbered
import com.kgzn.gamecenter.feature.downloader.utils.OnDuplicateStrategy.OverrideDownload
import com.kgzn.gamecenter.feature.downloader.utils.PathValidator
import com.kgzn.gamecenter.feature.downloader.utils.UrlUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.Throttler
import java.io.File

class DownloadManager(
    val dlListDb: IDownloadListDb,
    val partListDb: IDownloadPartListDb,
    val settings: DownloadSettings,
    val diskStat: IDiskStat,
    val emptyFileCreator: EmptyFileCreator,
    val client: DownloaderClient,
) : DownloadManagerMinimalControl {

    companion object {
        const val TAG = "DownloadManager"
    }

    val scope = CoroutineScope(SupervisorJob())

    private val _booted = MutableStateFlow(false)
    suspend fun awaitBoot() {
        _booted.first {
            it
        }
    }

    //make ready to resume download
    suspend fun boot() {
        if (_booted.value) return
        createJobForPendingDownloads()
        _booted.value = true
    }

    private val contextContainer = ContextProvider()

    private suspend fun createJobForPendingDownloads() {
        dlListDb.getAll().filter {
            it.status != DownloadStatus.Completed
        }.forEach {
            createJob(it).boot()
        }
    }

    var downloadJobs = listOf<DownloadJob>()
        private set

    private val dbAddSync = Mutex()

    suspend fun addDownload(
        newItem: DownloadItem,
        onDuplicateStrategy: OnDuplicateStrategy,
        context: DownloadItemContext = EmptyContext,
    ): Long {

        //make sure url is valid
        require(UrlUtils.isValidUrl(newItem.link)) {
            "url is not valid"
        }
        require(PathValidator.isValidPath(newItem.folder)) { "folder of new download is not valid: ${newItem.folder}" }
        require(PathValidator.canWriteToThisPath(newItem.folder)) { "can't write to this new download's folder: ${newItem.folder}" }
        require(FileNameValidator.isValidFileName(newItem.name)) { "name of new download is not valid: ${newItem.name}" }
//        thisLogger().info("adding download")
        val job = dbAddSync.withLock {
            val allDownloads = dlListDb.getAll()
            val duplicateFinder = DuplicateFilterByPath(File(newItem.folder, newItem.name))
            val foundItems = allDownloads.filter(duplicateFinder::isDuplicate)
            var removedItems = emptyList<DownloadItem>()
            if (foundItems.isNotEmpty()) {
                when (onDuplicateStrategy) {
                    AddNumbered -> {
                        //we do nothing here instead we increment file name after all if necessary
                    }

                    OverrideDownload -> {
                        foundItems.forEach {
                            deleteDownload(it.id, { true }, RemovedBy(DuplicateRemoval))
                        }
                        removedItems = foundItems
                    }

                    Abort -> {
                        error("Aborting add download that already exists")
                    }
                }
            }

            val name = FileNameUtil.numberedIfExists(
                File(newItem.folder, newItem.name)
            ).first { candidateNewFile ->
                val withSameDestination = allDownloads
                    .filter { it !in removedItems }
                    .find {
                        it.name == candidateNewFile.name && it.folder == candidateNewFile.parent
                    }
                withSameDestination == null
            }.name

            val id = dlListDb.getLastId() + 1
            val downloadItem = newItem.copy(
                id = id,
                name = name,
                dateAdded = System.currentTimeMillis(),
                startTime = null,
                completeTime = null,
                status = DownloadStatus.Added
            )
            dlListDb.add(downloadItem)
            createJob(downloadItem).apply { boot() }
        }
        contextContainer.setContext(job.id, context)
        onDownloadAdded(job.downloadItem)
//        thisLogger().info("this download added $downloadItem")
//        println("download created ${job.id}")
        return job.id
    }

    private val jobModificationLock = Any()
    private fun createJob(downloadItem: DownloadItem): DownloadJob {
        val job = DownloadJob(downloadItem, this, client)
//        thisLogger().info("download job for $id created")
        downloadJobs = downloadJobs + job
        return job
    }

    suspend fun deleteDownload(
        id: Long,
        alsoRemoveFile: (DownloadItem) -> Boolean,
        context: DownloadItemContext = EmptyContext,
    ) {
        runCatching { pause(id) }
        val itemToDelete = dlListDb.getById(id) ?: return
        val job = getDownloadJob(id) ?: run {
            createJob(itemToDelete).apply { boot() }
        }
        // at this point: job will be created (and booted) if it was not created before
        contextContainer.updateContext(id) { it + context }
        job.downloadRemoved(
            removeOutputFile = if (itemToDelete.status == DownloadStatus.Completed) {
                alsoRemoveFile(itemToDelete)
            } else {
                // always remove file if download is not finished!
                true
            },
        )
        deleteJob(job.id)
        dlListDb.remove(itemToDelete)
        partListDb.removeParts(id)
        listOfJobsEvents.tryEmit(
            DownloadManagerEvents.OnJobRemoved(itemToDelete, contextContainer.getContext(id))
        )
        contextContainer.removeContext(id)
    }

    private fun deleteJob(
        id: Long,
    ) {
        synchronized(jobModificationLock) {
            val jobToDelete = downloadJobs.find {
                it.id == id
            }
            jobToDelete?.let {
                it.close()
                downloadJobs = downloadJobs.minusElement(it)
            }
        }
    }

    suspend fun pause(id: Long, context: DownloadItemContext = EmptyContext) {
        val job = getDownloadJob(id)!!
        contextContainer.updateContext(id) { it + context }
        job.pause()
    }

    suspend fun resume(id: Long, context: DownloadItemContext = EmptyContext) {
        val job = getDownloadJob(id) ?: run {
            dlListDb.getById(id)?.let {
                createJob(it)
            }
        }
        job?.let {
            contextContainer.updateContext(id) { it + context }
            it.resume()
        }
    }

    suspend fun reset(id: Long, context: DownloadItemContext = EmptyContext) {
        val job = getDownloadJob(id) ?: run {
            dlListDb.getById(id)?.let {
                createJob(it)
            }
        }
        job?.let {
            contextContainer.updateContext(id) { it + context }
            it.reset()
        }
    }

    private fun getDownloadJob(id: Long): DownloadJob? {
//        thisLogger().info("finding job for $id")
        return downloadJobs.find {
            it.id == id
        }.also {
            if (it == null) {
//                thisLogger().info("there is no job for dl_$id")
            } else {
//                thisLogger().info("job found for dl_$id")
            }
        }
    }

    suspend fun getDownloadList(): List<DownloadItem> {
        return dlListDb.getAll()
    }

    fun getParts(id: Long): List<Part>? {
        return getDownloadJob(id)?.getParts()
    }

    fun onDownloadResuming(downloadItem: DownloadItem) {
        Log.d(TAG, "onDownloadResuming $downloadItem")
        listOfJobsEvents.tryEmit(
            DownloadManagerEvents.OnJobStarting(
                downloadItem,
                contextContainer.getContext(downloadItem.id)
            )
        )
    }

    fun onDownloadResumed(downloadItem: DownloadItem) {
        Log.d(TAG, "onDownloadResumed $downloadItem")
        listOfJobsEvents.tryEmit(
            DownloadManagerEvents.OnJobStarted(
                downloadItem,
                contextContainer.getContext(downloadItem.id)
            )
        )
    }

    fun onDownloadAdded(downloadItem: DownloadItem) {
        Log.d(TAG, "onDownloadAdded $downloadItem")
        listOfJobsEvents.tryEmit(
            DownloadManagerEvents.OnJobAdded(
                downloadItem,
                contextContainer.getContext(downloadItem.id)
            )
        )
    }

    fun onDownloadCanceled(downloadItem: DownloadItem, throwable: Throwable) {
        Log.d(TAG, "onDownloadCanceled $downloadItem $throwable")
        listOfJobsEvents.tryEmit(
            DownloadManagerEvents.OnJobCanceled(
                downloadItem,
                contextContainer.getContext(downloadItem.id), throwable
            )
        )
    }

    fun onDownloadFinished(downloadItem: DownloadItem) {
        Log.d(TAG, "onDownloadFinished $downloadItem")
        scope.launch {
            listOfJobsEvents.tryEmit(
                DownloadManagerEvents.OnJobCompleted(
                    downloadItem,
                    contextContainer.getContext(downloadItem.id)
                )
            )
            deleteJob(downloadItem.id)
        }
    }

    fun onDownloadItemChange(downloadItem: DownloadItem) {
        Log.d(TAG, "onDownloadItemChange $downloadItem")
        scope.launch {
            listOfJobsEvents.tryEmit(
                DownloadManagerEvents.OnJobChanged(
                    downloadItem,
                    contextContainer.getContext(downloadItem.id)
                )
            )
        }
    }

    override suspend fun startJob(id: Long, context: DownloadItemContext) {
        resume(id, context)
    }

    override suspend fun stopJob(id: Long, context: DownloadItemContext) {
        pause(id, context)
    }

    override fun canActivateJob(id: Long): Boolean {
        val job = downloadJobs.find { id == it.id }
        val status = job?.status?.value
//        println("job status $status")
        return status is DownloadJobStatus.CanBeResumed
    }

    suspend fun stopAll(
        context: DownloadItemContext = EmptyContext,
    ) {
        downloadJobs.filter {
            it.status.value == DownloadJobStatus.Downloading
        }.map {
            scope.async {
                pause(it.id, context)
            }
        }.awaitAll()
    }

    fun getActiveCount(): Int {
        return downloadJobs.filter {
            it.status.value is DownloadJobStatus.IsActive
        }.size
    }

    fun calculateOutputFile(downloadItem: DownloadItem): File {
        return File(downloadItem.folder, downloadItem.name)
    }

    fun getJobStatusOf(id: Long): DownloadJobStatus? {
        return downloadJobs.find {
            it.id == id
        }?.status?.value
    }

    override val listOfJobsEvents: MutableSharedFlow<DownloadManagerEvents> =
        MutableSharedFlow(extraBufferCapacity = 64)

    //global speed limiter
    internal val throttler = Throttler()
    fun limitGlobalSpeed(bytePerSecond: Long) {
        throttler.bytesPerSecond(bytePerSecond)
    }

    fun reloadSetting() {
        for (downloadJob in downloadJobs) {
            downloadJob.onPreferredConnectionCountChanged()
        }
    }

    suspend fun updateDownloadItem(id: Long, updater: (DownloadItem) -> Unit) {
        var wasCreated = false
        val job = getDownloadJob(id) ?: run {
            dlListDb.getById(id)?.let {
                wasCreated = true
                createJob(it)
            }
        } ?: return
        val updated = job.changeConfig(updater)
        if (wasCreated && updated.status == DownloadStatus.Completed) {
            deleteJob(job.id)
        }
        onDownloadItemChange(updated)
    }

}

private class ContextProvider {
    val contexts = mutableMapOf<Long, DownloadItemContext>()
    fun getContext(id: Long): DownloadItemContext {
        return contexts.getOrDefault(id, EmptyContext)
    }

    fun setContext(id: Long, context: DownloadItemContext) {
        if (context == EmptyContext) {
            removeContext(id)
            return
        }
        contexts[id] = context
    }

    fun removeContext(id: Long) {
        contexts.remove(id)
    }

    fun updateContext(id: Long, block: (DownloadItemContext) -> DownloadItemContext) {
        setContext(id, getContext(id).let(block))
    }
}
