package com.base.basepedo.pojo

import com.litesuits.orm.db.annotation.Column
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.annotation.Table
import com.litesuits.orm.db.enums.AssignType

/**
 * Created by base on 2016/1/30.
 */
@Table("step")
class StepData {
    // 指定自增，每个对象需要有一个主键
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    var id = 0

    @Column("today")
    var today: String? = null

    @Column("step")
    var step: String? = null

}
