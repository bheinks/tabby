package dev.heinkel.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.generated.tabby.tables.GroupUser.GROUP_USER
import org.jooq.generated.tabby.tables.daos.GroupUserDao
import org.jooq.generated.tabby.tables.pojos.Group
import org.jooq.generated.tabby.tables.pojos.GroupUser
import org.jooq.generated.tabby.tables.pojos.User

class GroupUserService(private val dslContext: DSLContext) {
    private val groupUserDao = GroupUserDao(dslContext.configuration())

    suspend fun createGroupUser(group: Group, user: User, isAdmin: Boolean = false) =
        withContext(Dispatchers.IO) {
            val groupUser = GroupUser(group.id, user.id, isAdmin)
            groupUserDao.insert(groupUser)
        }

    suspend fun updateAdmin(group: Group, user: User, isAdmin: Boolean) =
        withContext(Dispatchers.IO) {
            val groupUser = dslContext.selectFrom(GROUP_USER)
                .where(GROUP_USER.USER_ID.eq(user.id))
                .and(GROUP_USER.GROUP_ID.eq(group.id))
                .fetchOneInto(GroupUser::class.java)

            groupUser!!.admin = isAdmin
            groupUserDao.update(groupUser)
        }
}