package com.base.basepedo.utils

import android.content.Context
import com.litesuits.orm.LiteOrm
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.model.ConflictAlgorithm

/**
 * Created by base on 2016/1/31.
 */
object DbUtils {
    var DB_NAME: String? = null
    lateinit var liteOrm: LiteOrm
    fun createDb(_activity: Context?, DB_NAME: String) {
        this.DB_NAME = DB_NAME
        this.DB_NAME = "$DB_NAME.db"
        liteOrm = LiteOrm.newCascadeInstance(_activity, DB_NAME)
        liteOrm.setDebugged(true)
    }

    /**
     * 插入一条记录
     *
     * @param t
     */
    fun <T> insert(t: T) {
        liteOrm!!.save(t)
    }

    /**
     * 插入所有记录
     *
     * @param list
     */
    fun <T> insertAll(list: List<T>) {
        liteOrm!!.save(list)
    }

    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    fun <T> getQueryAll(cla: Class<T>): List<T> {
        return liteOrm!!.query(cla)
    }

    /**
     * 查询  某字段 等于 Value的值
     *
     * @param cla
     * @param field
     * @param value
     * @return
     */
    fun <T> getQueryByWhere(cla: Class<T>, field: String, value: Array<String>): List<T> {
        return liteOrm.query<T>(QueryBuilder(cla).where("$field=?", value))
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     *
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    fun <T> getQueryByWhereLength(cla: Class<T>, field: String, value: Array<String?>?, start: Int, length: Int): List<T> {
        return liteOrm.query<T>(QueryBuilder(cla).where("$field=?", value).limit(start, length))
    }
    /**
     * 删除所有 某字段等于 Vlaue的值
     * @param cla
     * @param field
     * @param value
     */
    //        public static <T> void deleteWhere(Class<T> cla,String field,String [] value){
    //            liteOrm.delete(cla, WhereBuilder.create().where(field + "=?", value));
    //        }
    /**
     * 删除所有
     *
     * @param cla
     */
    fun <T> deleteAll(cla: Class<T>) {
        liteOrm!!.deleteAll(cla)
    }

    /**
     * 仅在以存在时更新
     *
     * @param t
     */
    fun <T> update(t: T) {
        liteOrm!!.update(t, ConflictAlgorithm.Replace)
    }

    fun <T> updateALL(list: List<T>) {
        liteOrm!!.update(list)
    }

    fun closeDb() {
        liteOrm!!.close()
    }
}
