package br.com.listennow.interfaces

interface IkeepMainMethods<Model> {
    fun save(item: Model)
    fun edit(item: Model)
    fun delete(item: Model)
    fun findById(id: Int)
    fun listAll(): ArrayList<Model>
}