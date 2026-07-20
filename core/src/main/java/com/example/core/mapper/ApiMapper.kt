package com.example.core.mapper

interface ApiMapper<Domain, Entity> {
    fun mapToDomain(apiDto: Entity): Domain
}