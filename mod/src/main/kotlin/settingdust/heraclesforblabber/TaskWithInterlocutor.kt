package settingdust.heraclesforblabber

import net.minecraft.entity.Entity

interface TaskWithInterlocutor<T> {
    fun getInterlocutor(input: T): Entity {
        return input as Entity
    }
}
