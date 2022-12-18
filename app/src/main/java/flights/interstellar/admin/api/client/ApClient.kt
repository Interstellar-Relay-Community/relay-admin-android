package flights.interstellar.admin.api.client

import android.util.Log
import flights.interstellar.admin.api.ApClientInterface
import flights.interstellar.admin.api.client.activitypub.mastodon.MastodonApClient
import flights.interstellar.admin.api.client.activitypub.misskey.MisskeyApClient
import flights.interstellar.admin.api.client.activitypub.pleroma.PleromaApClient
import flights.interstellar.admin.api.pojo.InstanceInfo
import flights.interstellar.admin.api.pojo.InstanceUrl
import flights.interstellar.admin.api.pojo.InstanceUserInfo
import flights.interstellar.admin.api.pojo.UserHandle
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

val apClient = ApClient()

class ApClient : ApClientInterface {
    // TODO: Use some sort of smart way instead of creating boilerplates for every apClient instances
    private val mastodonApClient = MastodonApClient()
    private val misskeyApClient = MisskeyApClient()
    private val pleromaApClient = PleromaApClient()


    override suspend fun getUserInfo(
        instanceBaseUrl: InstanceUrl,
        handle: UserHandle
    ): InstanceUserInfo {
        val userInfo = supervisorScope {
            val mastodonParseResult = async {
                try {
                    mastodonApClient.getUserInfo(instanceBaseUrl, handle)
                } catch (_: Exception) { //TODO: Only catch deserialisation exception here
                    null
                }
            }
            val misskeyParseResult = async {
                try {
                    misskeyApClient.getUserInfo(instanceBaseUrl, handle)
                } catch (_: Exception) { //TODO: Only catch deserialisation exception here
                    null
                }
            }
            val pleromaParseResult = async {
                try {
                    pleromaApClient.getUserInfo(instanceBaseUrl, handle)
                } catch (_: Exception) { //TODO: Only catch deserialisation exception here
                    null
                }
            }

            //TODO: Use nodeinfo to determine suitable parser instead of trying everything
            return@supervisorScope mastodonParseResult.await() ?: misskeyParseResult.await()
            ?: pleromaParseResult.await()
        }

        return userInfo ?: throw RuntimeException("All parsers failed, unsupported instance type?")
    }

    override suspend fun getInstanceInfo(instanceBaseUrl: InstanceUrl): InstanceInfo {
        val instanceInfo = supervisorScope {
            val mastodonParseResult = async {
                try {
                    mastodonApClient.getInstanceInfo(instanceBaseUrl)
                } catch (e: Exception) { //TODO: Only catch deserialisation exception here
                    null
                }
            }
            val misskeyParseResult = async {
                try {
                    misskeyApClient.getInstanceInfo(instanceBaseUrl)
                } catch (e: Exception) { //TODO: Only catch deserialisation exception here
                    null
                }
            }
            val pleromaParseResult = async {
                try {
                    pleromaApClient.getInstanceInfo(instanceBaseUrl)
                } catch (_: Exception) { //TODO: Only catch deserialisation exception here
                    null
                }
            }

            //TODO: Use nodeinfo to determine suitable parser instead of trying everything
            return@supervisorScope mastodonParseResult.await() ?: misskeyParseResult.await()
            ?: pleromaParseResult.await()
        }

        Log.v("ApClient", "Result($instanceBaseUrl): $instanceInfo")
        return instanceInfo
            ?: throw RuntimeException("All parsers failed, unsupported instance type?")
    }
}