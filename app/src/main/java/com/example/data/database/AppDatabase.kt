package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AllocationDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.ExpenseDao
import com.example.data.dao.GoalDao
import com.example.data.dao.SettingsDao
import com.example.data.model.AllocationEntity
import com.example.data.model.AllocationSplitEntity
import com.example.data.model.AppSettingEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.GoalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoryEntity::class,
        AllocationEntity::class,
        AllocationSplitEntity::class,
        ExpenseEntity::class,
        GoalEntity::class,
        AppSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun allocationDao(): AllocationDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun goalDao(): GoalDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_allocator_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(database: AppDatabase) {
                val categoryDao = database.categoryDao()
                val settingsDao = database.settingsDao()
                val goalDao = database.goalDao()

                // Default currency: Botswana Pula (P)
                settingsDao.setSetting(AppSettingEntity(key = "currency_symbol", value = "P"))

                // Default categories as specified by the user:
                // 40% — Future / Zimbabwe Fund
                // 30% — Tools & Skills
                // 20% — Personal / Fun
                // 10% — Family / Miscellaneous
                val defaultCategories = listOf(
                    CategoryEntity(
                        name = "Future / Zimbabwe Fund",
                        percentage = 40.0,
                        description = "Savings, investments & capital",
                        colorHex = "#10B981", // Emerald Green
                        displayOrder = 0
                    ),
                    CategoryEntity(
                        name = "Tools & Skills",
                        percentage = 30.0,
                        description = "Equipment, tools, courses & knowledge",
                        colorHex = "#F59E0B", // Amber Gold
                        displayOrder = 1
                    ),
                    CategoryEntity(
                        name = "Personal / Fun",
                        percentage = 20.0,
                        description = "Living, enjoyment & personal needs",
                        colorHex = "#6366F1", // Indigo
                        displayOrder = 2
                    ),
                    CategoryEntity(
                        name = "Family / Miscellaneous",
                        percentage = 10.0,
                        description = "Family support & emergency buffer",
                        colorHex = "#EC4899", // Rose
                        displayOrder = 3
                    )
                )
                categoryDao.insertCategories(defaultCategories)

                // Default suggested goals from user's prompt:
                val categories = categoryDao.getCategoriesList()
                val futureCat = categories.find { it.name.contains("Future", ignoreCase = true) }
                val toolsCat = categories.find { it.name.contains("Tools", ignoreCase = true) }
                val famCat = categories.find { it.name.contains("Family", ignoreCase = true) }

                goalDao.insertGoals(
                    listOf(
                        GoalEntity(
                            title = "Zimbabwe Business Capital",
                            targetAmount = 10000.0,
                            linkedCategoryId = futureCat?.id
                        ),
                        GoalEntity(
                            title = "Tools Fund",
                            targetAmount = 5000.0,
                            linkedCategoryId = toolsCat?.id
                        ),
                        GoalEntity(
                            title = "Emergency Fund",
                            targetAmount = 2000.0,
                            linkedCategoryId = famCat?.id
                        )
                    )
                )
            }
        }
    }
}
