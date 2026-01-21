## ProductMappersTest (unit)

**File:** `composeApp/src/commonTest/kotlin/org/example/kmp/feature/catalog/data/mapper/ProductMappersTest.kt`

**Covers:**
- `dtoToEntity()` maps all fields correctly and sets `updatedAt/lastSeenAt`
- `entityToDomain()` maps all fields correctly and applies `isFavorite`
- `entitiesToDomain()` correctly marks favorites based on `favoriteIds`
- `images` mapping works for both `emptyList()` and non-empty lists

**Why:**
Mappers are pure and deterministic, so unit tests guarantee stable and predictable data transformation across layers.

## UseCases (unit)

**File:** `composeApp/src/commonTest/kotlin/org/example/kmp/feature/catalog/domain/usecase/CatalogUseCasesTest.kt`

**Covers:**
- `ObserveCatalogUseCase` delegates to `CatalogRepository.observeCatalogPage()`
- `ObserveFavoritesUseCase` delegates to `CatalogRepository.observeFavorites()`
- `ObserveProductDetailsUseCase` delegates to `CatalogRepository.observeProductDetails()`
- `SyncCatalogPageUseCase` delegates to `CatalogRepository.syncCatalogPage()`
- `ToggleFavoriteUseCase` delegates to `CatalogRepository.toggleFavorite()`

**Why:**
Use cases encapsulate business actions and should remain small, predictable, and easy to verify with unit tests.

## CatalogListComponentImplTest (unit)

**File:** `composeApp/src/commonTest/kotlin/org/example/kmp/feature/catalog/presentation/list/CatalogListComponentImplTest.kt`

**Covers:**
- Component observes catalog items from `ObserveCatalogUseCase` (DB Flow → UI state)
- Initial synchronization triggers `SyncCatalogPageUseCase(pageIndex=0)`
- Pagination via `onLoadNextPage()` triggers next page sync and updates observed item limit
- `onToggleFavorite()` delegates to `ToggleFavoriteUseCase`

**Why:**
This verifies the core presentation logic: offline-first reactive UI state powered by DB flows + explicit user-driven sync.

## Compose UI tests (androidTest)

**Files:**
- `composeApp/src/androidTest/kotlin/org/example/kmp/ui/CatalogSmokeTest.kt`

**Covers:**
- App launches successfully
- Catalog screen is displayed
- Navigation to Favorites works
- Favorites empty state is shown when no items are favorited

**Why:**
Ensures the basic UI flow works on a real Android runtime (instrumented):
navigation, screen rendering, and key user-visible states behave correctly.

## CatalogRepositoryImplIntegrationTest (integration)

**File:** `composeApp/src/androidTest/kotlin/org/example/kmp/feature/catalog/data/repository/CatalogRepositoryImplIntegrationTest.kt`

**Covers:**
- `syncCatalogPage()` writes products from Fake API into an in-memory Room database
- `observeCatalogPage()` emits items from DB after sync (Flow-driven)
- `toggleFavorite()` persists favorite state in DB and updates observed flows
- `observeFavorites()` returns only favorited products (based on join with favorites table)
- `observeProductDetails()` returns product details with correct `isFavorite` flag

**Why:**
Validates the real offline-first data pipeline end-to-end:
Fake API → Repository → Room (in-memory) → Flow observers.
Confirms the "Single Source of Truth" principle: UI observes DB only, sync never updates UI directly.