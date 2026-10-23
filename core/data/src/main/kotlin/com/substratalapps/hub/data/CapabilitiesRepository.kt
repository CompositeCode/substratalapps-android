package com.substratalapps.hub.data

import com.substratalapps.hub.network.ApiException
import com.substratalapps.hub.network.SubstratalApi
import com.substratalapps.hub.network.User
import com.substratalapps.hub.network.apiCall
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * What the signed-in person may see and do (CLIENT-ARCHITECTURE §5). Navigation and every action
 * read from here; the server stays the authority and a 403 triggers [refresh].
 */
data class Capabilities(
    val user: User? = null,
    val permissions: Set<String> = emptySet(),
    val ownedAppIds: List<String> = emptyList(),
    /** False until the first refresh after sign-in finishes (successfully or not). */
    val loaded: Boolean = false,
) {
    fun has(permission: String) = permission in permissions

    val canListUsers get() = has("users.list")
    val canManageUsers get() = has("users.manage")
    val canManageEntitlements get() = has("entitlements.manage")
    val canManageRoles get() = has("roles.manage")
    val canViewAudit get() = has("audit.view")
    val canManageApplications get() = has("applications.manage")
    val canManageApiKeys get() = has("api_keys.manage")
    val canManageWebhooks get() = has("webhooks.manage")
    val canManageTenants get() = has("tenants.manage")
    val canSendNotifications get() = has("notifications.send")

    /** Proposed `operations.view` (API issue #47). Until it exists, superadmin-equivalent rights stand in. */
    val canViewOperations get() = has("operations.view") || (canManageApplications && canManageRoles)

    val ownsApps get() = ownedAppIds.isNotEmpty()

    val showsAdministration
        get() = canListUsers || canManageEntitlements || canManageRoles || canViewAudit || canSendNotifications || ownsApps

    val showsDeveloper
        get() = ownsApps || canManageApplications || canManageApiKeys || canManageWebhooks || canManageTenants
}

@Singleton
class CapabilitiesRepository @Inject constructor(private val api: SubstratalApi) {
    private val state = MutableStateFlow(Capabilities())
    val capabilities: StateFlow<Capabilities> = state.asStateFlow()

    /** Suspends until capabilities for the current session are known. */
    suspend fun awaitLoaded(): Capabilities = capabilities.first { it.loaded }

    suspend fun refresh() {
        try {
            load()
        } catch (e: Exception) {
            // Keep whatever we had, but mark it loaded so screens don't wait forever.
            state.value = state.value.copy(loaded = true)
            throw e
        }
    }

    private suspend fun load() = coroutineScope {
        val user = async { apiCall { api.getUser("me") } }
        val assignments = async { apiCall { api.listUserRoles("me") } }
        // 403 here just means "no admin permissions" (CLIENT-ARCHITECTURE §5).
        val roles = async { runCatching { apiCall { api.listRoles("platform") } }.getOrNull() }
        val owned = async { runCatching { apiCall { api.listApplications(limit = 50, owned = true) } }.getOrNull() }

        val heldRoleIds = assignments.await().data.filter { it.applicationId == null }.map { it.roleId }.toSet()
        val permissions = roles.await()?.data.orEmpty()
            .filter { it.id in heldRoleIds }
            .flatMap { it.permissions }
            .toSet()
        state.value = Capabilities(
            user = user.await(),
            permissions = permissions,
            ownedAppIds = owned.await()?.data.orEmpty().map { it.id },
            loaded = true,
        )
    }

    fun clear() {
        state.value = Capabilities()
    }

    /** Refreshes after a 403 without surfacing errors. */
    suspend fun onForbidden(e: ApiException) {
        if (e.isForbidden) runCatching { refresh() }
    }
}
