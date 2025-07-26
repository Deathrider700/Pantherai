package com.pantherai.app.data.remote

import com.pantherai.app.data.model.HistoryItem
import com.pantherai.app.data.model.UserPreferences
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.PostgrestRequestBuilder
import io.github.jan.supabase.postgrest.query.Returning
import io.github.jan.supabase.postgrest.query.Single
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import io.github.jan.supabase.postgrest.query.filter.eq
import io.github.jan.supabase.postgrest.query.filter.order
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseService @Inject constructor() {
    
    private val supabase = createSupabaseClient(
        supabaseUrl = "YOUR_SUPABASE_URL", // Replace with your Supabase URL
        supabaseKey = "YOUR_SUPABASE_ANON_KEY" // Replace with your Supabase anon key
    ) {
        install(GoTrue) {
            scheme = "com.pantherai.app"
            host = "login"
        }
        install(Postgrest)
        install(Storage)
    }
    
    // Authentication
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            supabase.gotrue.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase.gotrue.loginWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return try {
            supabase.gotrue.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCurrentUser() = supabase.gotrue.currentUserOrNull()
    
    fun getAuthStateFlow() = supabase.gotrue.currentSession
    
    // History
    suspend fun saveHistory(historyItem: HistoryItem): Result<Unit> {
        return try {
            supabase.postgrest["history"].insert(historyItem)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getHistory(
        userId: String,
        type: String? = null,
        limit: Int = 50
    ): Result<List<HistoryItem>> {
        return try {
            val query = supabase.postgrest["history"]
                .select(columns = Columns.all) {
                    eq("user_id", userId)
                    if (type != null) {
                        eq("type", type)
                    }
                    order("created_at", Order.DESCENDING)
                }
                .limit(limit)
            
            val result = query.decodeList<HistoryItem>()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteHistory(historyId: String): Result<Unit> {
        return try {
            supabase.postgrest["history"]
                .delete {
                    eq("id", historyId)
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // User Preferences
    suspend fun getUserPreferences(userId: String): Result<UserPreferences?> {
        return try {
            val result = supabase.postgrest["user_preferences"]
                .select(columns = Columns.all) {
                    eq("user_id", userId)
                }
                .decodeSingleOrNull<UserPreferences>()
            
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserPreferences(preferences: UserPreferences): Result<Unit> {
        return try {
            supabase.postgrest["user_preferences"]
                .upsert(preferences) {
                    onConflict = "user_id"
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // File Storage (for audio files)
    suspend fun uploadFile(
        bucket: String,
        path: String,
        data: ByteArray
    ): Result<String> {
        return try {
            supabase.storage[bucket].upload(path, data)
            val publicUrl = supabase.storage[bucket].publicUrl(path)
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun downloadFile(bucket: String, path: String): Result<ByteArray> {
        return try {
            val data = supabase.storage[bucket].download(path)
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 