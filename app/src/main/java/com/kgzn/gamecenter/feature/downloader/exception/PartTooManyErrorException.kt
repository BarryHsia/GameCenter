package com.kgzn.gamecenter.feature.downloader.exception

import com.kgzn.gamecenter.feature.downloader.part.Part

class PartTooManyErrorException(
    part: Part,
    override val cause: Throwable
) : Exception(
    "this part $part have too many errors",
    cause,
)
