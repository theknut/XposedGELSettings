package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class ObfuscationHelper extends HooksBaseClass {

    public static final int GNL_3_3_11 = 300303110;
    public static final int GNL_3_4_15 = 300304150;
    public static final int GNL_3_5_14 = 300305140;

    public static int getVersionIndex(int version) {

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            if (version >= GNL_3_5_14) {
                return 2;
            } else if (version >= GNL_3_4_15) {
                return 1;
            } else if (version <= GNL_3_3_11) {
                return 0;
            }
        }

        return 0;
    }

    public static void init(LoadPackageParam lpparam, int versionIdx) {
        long time = System.currentTimeMillis();

        ClassNames.initNames(versionIdx);
        Classes.hookAllClasses(lpparam);
        Methods.initMethodNames(versionIdx);
        Fields.initFieldNames(versionIdx);

        if (DEBUG) log("Initialized ObfuscationHelper in " + (System.currentTimeMillis() - time) + "ms");
    }

    public static class ClassNames {

        public static String LAUNCHER,
                WORKSPACE,
                WORKSPACE_STATE,
                DEVICE_PROFILE,
                CELL_LAYOUT,
                CELL_LAYOUT_CELL_INFO,
                CELL_LAYOUT_LAYOUT_PARAMS,
                PAGED_VIEW,
                PAGED_VIEW_ICON,
                PAGED_VIEW_CELL_LAYOUT,
                PAGED_VIEW_WITH_DRAGGABLE_ITEMS,
                APPS_CUSTOMIZE_CONTENT_TYPE,
                APPS_CUSTOMIZE_CELL_LAYOUT,
                APPS_CUSTOMIZE_LAYOUT,
                APPS_CUSTOMIZE_PAGED_VIEW,
                APPS_CUSTOMIZE_TAB_HOST,
                WALLPAPER_OFFSET_INTERPOLATOR,
                FOLDER,
                FOLDER_ICON,
                HOTSEAT,
                START_SETTINGS_ONCLICK,
                DRAG_SOURCE,
                ITEM_INFO,
                APP_INFO,
                SHORTCUT_INFO,
                SEARCH_DROP_TARGET_BAR,
                ICON_CACHE,
                UTILITIES,
                CACHE_ENTRY,
                LOADER_TASK,
                FOLDER_INFO,
                LAUNCHER_MODEL,
                APP_WIDGET_RESIZE_FRAME,
                ITEM_CONFIGURATION,
                LAUNCHER_APPWIDGET_INFO,
                DRAG_LAYER,
                LAUNCHER_APP_WIDGET_HOST_VIEW,
                BUBBLE_TEXT_VIEW,
                USER_HANDLE,
                ADB,
                GEL,
                NOW_OVERLAY,
                SEARCH_OVERLAY_IMPL,
                GSA_CONFIG_FLAGS;

        public static void initNames(int idx) {

            String launcherPackage = "com.android.launcher3.";
            if (Common.HOOKED_PACKAGE.equals("com.android.launcher2")) {
                launcherPackage = "com.android.launcher2.";
            }

            String[] _LAUNCHER = {launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher"},
                    _WORKSPACE = {launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace"},
                    _WORKSPACE_STATE = {_WORKSPACE[0] + "$State", "zc", "aco"},
                    _DEVICE_PROFILE = {launcherPackage + "DeviceProfile", "mz", "qi"},
                    _CELL_LAYOUT = {launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout"},
                    _CELL_LAYOUT_CELL_INFO = {_CELL_LAYOUT[0] + "$CellInfo", "lz", "pi"},
                    _CELL_LAYOUT_LAYOUT_PARAMS = {_CELL_LAYOUT[0] + "$LayoutParams", _CELL_LAYOUT[0] + "$LayoutParams", _CELL_LAYOUT[0] + "$LayoutParams"},
                    _PAGED_VIEW = {launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView"},
                    _PAGED_VIEW_ICON = {launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon"},
                    _PAGED_VIEW_CELL_LAYOUT = {launcherPackage + "PagedViewCellLayout", "vd", "yo"},
                    _PAGED_VIEW_WITH_DRAGGABLE_ITEMS = {launcherPackage + "PagedViewWithDraggableItems", "vl", "yw"},
                    _APPS_CUSTOMIZE_CELL_LAYOUT = {launcherPackage + "AppsCustomizeCellLayout", "kw", "yr"},
                    _APPS_CUSTOMIZE_LAYOUT = {launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout"},
                    _APPS_CUSTOMIZE_PAGED_VIEW = {launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView"},
                    _APPS_CUSTOMIZE_TAB_HOST = {launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost"},
                    _APPS_CUSTOMIZE_CONTENT_TYPE = {_APPS_CUSTOMIZE_PAGED_VIEW[0] + "$ContentType", "lf", "oo"},
                    _WALLPAPEROFFSETINTERPOLATOR = {_WORKSPACE[0] + "$WallpaperOffsetInterpolator", "zd", "acp"},
                    _FOLDER = {launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder"},
                    _FOLDER_ICON = {launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon"},
                    _HOTSEAT = {launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat"},
                    _START_SETTINGS_ONCLICK = { "", "pu", "td" },
                    _DRAG_SOURCE = {launcherPackage + "DragSource", "nn", "qw"}, // first parameter in onDragStart of SearchDropTargetBar
                    _ITEM_INFO = {launcherPackage + "ItemInfo", "pr", "ta"},
                    _APP_INFO = {launcherPackage + "AppInfo", "kr", "ob"},
                    _SHORTCUT_INFO = {launcherPackage + "ShortcutInfo", "vz", "zl"},
                    _SEARCH_DROP_TARGET_BAR = {launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar"},
                    _ICON_CACHE = {launcherPackage + "IconCache", "pk", "ss"},
                    _UTILITIES = {launcherPackage + "Utilities", "wi", "zu"},
                    _CACHE_ENTRY = {_ICON_CACHE[0] + "$CacheEntry", "pl", "st"},
                    _LAUNCHER_MODEL = {launcherPackage + "LauncherModel", "sg", "vq"},
                    _LOADER_TASK = {_LAUNCHER_MODEL[0] + "$LoaderTask", "tb", "wl"},
                    _FOLDER_INFO = {launcherPackage + "FolderInfo", "oz", "sh"},
                    _APP_WIDGET_RESIZE_FRAME = {launcherPackage + "AppWidgetResizeFrame", "ks", "oc"},
                    _ITEM_CONFIGURATION = {_CELL_LAYOUT[0] + "$ItemConfiguration", "ma", "pj"},
                    _LAUNCHER_APPWIDGET_INFO = {launcherPackage + "LauncherAppWidgetInfo", "rv", "vf"},
                    _DRAG_LAYER = {launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer"},
                    _LAUNCHER_APP_WIDGET_HOST_VIEW = {launcherPackage + "LauncherAppWidgetHostView", "ru", "ve"},
                    _BUBBLE_TEXT_VIEW = {launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView"},
                    _USER_HANDLE = {"", "", "adl"},
                    _ADB = {"", "", "adb"},
                    _GEL = {"com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL"},
                    _NOW_OVERLAY = {"com.google.android.sidekick.shared.client.NowOverlay", "dzk", "enc"},
                    _SEARCH_OVERLAY_IMPL = {"com.google.android.search.gel.SearchOverlayImpl", "ccu", "cmh"},
                    _GSA_CONFIG_FLAGS = {"com.google.android.search.core.GsaConfigFlags", "ayc", "bgr"}; // Unknown string array encoding

            LAUNCHER = _LAUNCHER[idx];
            WORKSPACE = _WORKSPACE[idx];
            WORKSPACE_STATE = _WORKSPACE_STATE[idx];
            DEVICE_PROFILE = _DEVICE_PROFILE[idx];
            CELL_LAYOUT = _CELL_LAYOUT[idx];
            CELL_LAYOUT_CELL_INFO = _CELL_LAYOUT_CELL_INFO[idx];
            CELL_LAYOUT_LAYOUT_PARAMS = _CELL_LAYOUT_LAYOUT_PARAMS[idx];
            PAGED_VIEW = _PAGED_VIEW[idx];
            PAGED_VIEW_ICON = _PAGED_VIEW_ICON[idx];
            PAGED_VIEW_CELL_LAYOUT = _PAGED_VIEW_CELL_LAYOUT[idx];
            PAGED_VIEW_WITH_DRAGGABLE_ITEMS = _PAGED_VIEW_WITH_DRAGGABLE_ITEMS[idx];
            APPS_CUSTOMIZE_CELL_LAYOUT = _APPS_CUSTOMIZE_CELL_LAYOUT[idx];
            APPS_CUSTOMIZE_LAYOUT = _APPS_CUSTOMIZE_LAYOUT[idx];
            APPS_CUSTOMIZE_PAGED_VIEW = _APPS_CUSTOMIZE_PAGED_VIEW[idx];
            APPS_CUSTOMIZE_TAB_HOST = _APPS_CUSTOMIZE_TAB_HOST[idx];
            APPS_CUSTOMIZE_CONTENT_TYPE = _APPS_CUSTOMIZE_CONTENT_TYPE[idx];
            WALLPAPER_OFFSET_INTERPOLATOR = _WALLPAPEROFFSETINTERPOLATOR[idx];
            FOLDER = _FOLDER[idx];
            FOLDER_ICON = _FOLDER_ICON[idx];
            HOTSEAT = _HOTSEAT[idx];
            START_SETTINGS_ONCLICK = _START_SETTINGS_ONCLICK[idx];
            DRAG_SOURCE = _DRAG_SOURCE[idx];
            ITEM_INFO = _ITEM_INFO[idx];
            APP_INFO = _APP_INFO[idx];
            SHORTCUT_INFO = _SHORTCUT_INFO[idx];
            SEARCH_DROP_TARGET_BAR = _SEARCH_DROP_TARGET_BAR[idx];
            ICON_CACHE = _ICON_CACHE[idx];
            UTILITIES = _UTILITIES[idx];
            CACHE_ENTRY = _CACHE_ENTRY[idx];
            LAUNCHER_MODEL = _LAUNCHER_MODEL[idx];
            LOADER_TASK = _LOADER_TASK[idx];
            FOLDER_INFO = _FOLDER_INFO[idx];
            APP_WIDGET_RESIZE_FRAME = _APP_WIDGET_RESIZE_FRAME[idx];
            ITEM_CONFIGURATION = _ITEM_CONFIGURATION[idx];
            LAUNCHER_APPWIDGET_INFO = _LAUNCHER_APPWIDGET_INFO[idx];
            DRAG_LAYER = _DRAG_LAYER[idx];
            LAUNCHER_APP_WIDGET_HOST_VIEW = _LAUNCHER_APP_WIDGET_HOST_VIEW[idx];
            BUBBLE_TEXT_VIEW = _BUBBLE_TEXT_VIEW[idx];
            USER_HANDLE = _USER_HANDLE[idx];
            ADB = _ADB[idx];
            GEL = _GEL[idx];
            NOW_OVERLAY = _NOW_OVERLAY[idx];
            SEARCH_OVERLAY_IMPL = _SEARCH_OVERLAY_IMPL[idx];
            GSA_CONFIG_FLAGS = _GSA_CONFIG_FLAGS[idx];
        }
    }

    public static class Classes {

        static public Class<?> Launcher,
                Workspace,
                AppInfo,
                AppsCustomizePagedView,
                CellLayout,
                WallpaperOffsetInterpolator,
                PagedViewIcon,
                DeviceProfile,
                AppsCustomizeLayout,
                AppsCustomizeTabHost,
                Folder,
                FolderIcon,
                PagedViewWithDraggableItems,
                PagedView,
                GELClass,
                NowOverlay,
                SearchOverlayImpl,
                SearchDropTargetBar,
                DragSource,
                CellLayoutLayoutParams,
                CellLayoutCellInfo,
                WorkspaceState,
                Hotseat,
                StartSettingsOnClick,
                AppsCustomizeContentType,
                ShortcutInfo,
                IconCache,
                Utilities,
                CacheEntry,
                ItemInfo,
                LoaderTask,
                FolderInfo,
                LauncherModel,
                AppWidgetResizeFrame,
                ItemConfiguration,
                UserHandle,
                Adb,
                LauncherAppWidgetInfo,
                DragLayer,
                LauncherAppWidgetHostView,
                GSAConfigFlags,
                BubbleTextView;

        public static void hookAllClasses(LoadPackageParam lpparam) {
            Launcher = findClass(ClassNames.LAUNCHER, lpparam.classLoader);
            Workspace = findClass(ClassNames.WORKSPACE, lpparam.classLoader);
            AppsCustomizePagedView = findClass(ClassNames.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
            CellLayout = findClass(ClassNames.CELL_LAYOUT, lpparam.classLoader);
            CellLayoutLayoutParams = findClass(ClassNames.CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
            WallpaperOffsetInterpolator = findClass(ClassNames.WALLPAPER_OFFSET_INTERPOLATOR, lpparam.classLoader);
            PagedViewIcon = findClass(ClassNames.PAGED_VIEW_ICON, lpparam.classLoader);
            DeviceProfile = findClass(ClassNames.DEVICE_PROFILE, lpparam.classLoader);
            AppInfo = findClass(ClassNames.APP_INFO, lpparam.classLoader);

            if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
                AppsCustomizeLayout = findClass(ClassNames.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
            } else {
                AppsCustomizeTabHost = findClass(ClassNames.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
            }

            CellLayoutCellInfo = findClass(ClassNames.CELL_LAYOUT_CELL_INFO, lpparam.classLoader);
            AppsCustomizeContentType = findClass(ClassNames.APPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
            Folder = findClass(ClassNames.FOLDER, lpparam.classLoader);
            PagedViewWithDraggableItems = findClass(ClassNames.PAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
            PagedView = findClass(ClassNames.PAGED_VIEW, lpparam.classLoader);
            FolderIcon = findClass(ClassNames.FOLDER_ICON, lpparam.classLoader);
            Hotseat = findClass(ClassNames.HOTSEAT, lpparam.classLoader);
            DragSource = findClass(ClassNames.DRAG_SOURCE, lpparam.classLoader);
            ShortcutInfo = findClass(ClassNames.SHORTCUT_INFO, lpparam.classLoader);
            SearchDropTargetBar = findClass(ClassNames.SEARCH_DROP_TARGET_BAR, lpparam.classLoader);
            IconCache = findClass(ClassNames.ICON_CACHE, lpparam.classLoader);
            Utilities = findClass(ClassNames.UTILITIES, lpparam.classLoader);
            CacheEntry = findClass(ClassNames.CACHE_ENTRY, lpparam.classLoader);
            ItemInfo = findClass(ClassNames.ITEM_INFO, lpparam.classLoader);
            LoaderTask = findClass(ClassNames.LOADER_TASK, lpparam.classLoader);
            FolderInfo = findClass(ClassNames.FOLDER_INFO, lpparam.classLoader);
            LauncherModel = findClass(ClassNames.LAUNCHER_MODEL, lpparam.classLoader);
            AppWidgetResizeFrame = findClass(ClassNames.APP_WIDGET_RESIZE_FRAME, lpparam.classLoader);
            ItemConfiguration = findClass(ClassNames.ITEM_CONFIGURATION, lpparam.classLoader);
            LauncherAppWidgetInfo = findClass(ClassNames.LAUNCHER_APPWIDGET_INFO, lpparam.classLoader);
            DragLayer = findClass(ClassNames.DRAG_LAYER, lpparam.classLoader);
            LauncherAppWidgetHostView = findClass(ClassNames.LAUNCHER_APP_WIDGET_HOST_VIEW, lpparam.classLoader);
            BubbleTextView = findClass(ClassNames.BUBBLE_TEXT_VIEW, lpparam.classLoader);

            if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {
                GELClass = findClass(ClassNames.GEL, lpparam.classLoader);
                NowOverlay = findClass(ClassNames.NOW_OVERLAY, lpparam.classLoader);
                SearchOverlayImpl = findClass(ClassNames.SEARCH_OVERLAY_IMPL, lpparam.classLoader);
                GSAConfigFlags = findClass(ClassNames.GSA_CONFIG_FLAGS, lpparam.classLoader);

                if (Common.PACKAGE_OBFUSCATED) {
                    WorkspaceState = findClass(ClassNames.WORKSPACE_STATE, lpparam.classLoader);

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_5_14) {
                        UserHandle = findClass(ClassNames.USER_HANDLE, lpparam.classLoader);
                        Adb = findClass(ClassNames.ADB, lpparam.classLoader);
                    }

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_4_15) {
                        StartSettingsOnClick = findClass(ClassNames.START_SETTINGS_ONCLICK, lpparam.classLoader);
                    }
                }
            }
        }
    }

    public static class Methods {

        public static String applyFromApplicationInfo,
                lGetApplicationContext,
                lIsRotationEnabled,
                clAddViewToCellLayout,
                woiSyncWithScroll,
                wStartDrag,
                acpvOnPackagesUpdated,
                lGetSearchbar,
                lGetQsbBar,
                pvPageBeginMoving,
                pvPageEndMoving,
                sdtbOnDragStart,
                sdtbOnDragEnd,
                lHasCustomContentToLeft,
                hideAppsCustomizeHelper,
                launcherShowWorkspace,
                launcherShowAllApps,
                workspaceMoveToDefaultScreen,
                btvSetShadowsEnabled,
                wsOverScroll,
                lFinishBindingItems,
                dpGetWorkspacePadding,
                lIsAllAppsVisible,
                lGetOpenFolder,
                wIsOnOrMovingToCustomContent,
                wEnterOverviewMode,
                wMoveToCustomContentScreen,
                pvSnapToPage,
                lOpenFolder,
                wOnDragEnd,
                wOnDragStart,
                lCloseFolder,
                acthOnTabChanged,
                wSetCurrentPage,
                acpvOverScroll,
                acpvSetCurrentPage,
                dpUpdateFromConfiguration,
                acthSetInsets,
                wSnapToPage,
                soiSetSearchStarted,
                noOnShow,
                wOnLauncherTransitionEnd,
                fOnRemove,
                fOnAdd,
                fReplaceFolderWithFinalItem,
                fGetItemsInReadingOrder,
                clGetShortcutsAndWidgets,
                acthGetContentTypeForTabTag,
                wOnTransitionPrepare,
                siGetIntent,
                icGetFullResIcon,
                uCreateIconBitmap,
                icCacheLocked,
                clMarkCellsForView,
                lmCheckItemPlacement,
                acpvBeginDragging,
                lBindAppsUpdated,
                lmIsShortcutInfoUpdateable,
                clAttemptPushInDirection,
                acpvSetApps,
                acpvUpdateApps,
                acpvRemoveApps,
                lSetWorkspaceBackground,
                lGetDragLayer,
                dlAddResizeFrame,
                gsaShouldAlwaysShowHotwordHint,
                btvCreateGlowingOutline,
                lmDeleteItemFromDatabase,
                lmDeleteFolderContentsFromDatabase,
                siGetIcon;

        public static void initMethodNames(int idx) {

            String[] _applyFromApplicationInfo = {"applyFromApplicationInfo", "a", "a"},
                    _launcherGetApplicationContext = {"getApplicationContext", "getApplicationContext", "getApplicationContext"},
                    _launcherIsRotationEnabled = {"isRotationEnabled", "gC", "hr"}, // getBoolean - single line method
                    _celllayoutAddViewToCellLayout = {"addViewToCellLayout", "a", "a"}, // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
                    _wallpaperoffsetinterpolatorSyncWithScroll = {"syncWithScroll", "kf", "la"}, // computeScroll in Workspace
                    _workspaceStartDrag = {"startDrag", "a", "a"}, // isInTouchMode
                    _acpvOnPackagesUpdated = {"onPackagesUpdated", "a", "a"}, // "can not fit on this device"
                    _launcherGetSearchbar = {"getSearchBar", "fZ", "gO"}, // return SearchDropTargetBar in Launcher
                    _launcherGetQsbBar = {"getQsbBar", "gw", "hl"}, // inflate followed by addView
                    _pagedviewPageBeginMoving = {"pageBeginMoving", "ii", "iY"}, // protected void
                    _pagedviewPageEndMoving = {"pageEndMoving", "ij", "iZ"},
                    _sdtbOnDragStart = {"onDragStart", "a", "a"}, // twice .start in the method
                    _sdtbOnDragEnd = {"onDragEnd", "dt", "ei"}, // twice .reverse
                    _launcherHasCustomContentToLeft = {"hasCustomContentToLeft", "fL", "gA"}, // isEmpty
                    _hideAppsCustomizeHelper = {"hideAppsCustomizeHelper", "a", "a"},
                    _launcherShowWorkspace = {"showWorkspace", "a", "a"}, // boolean paramBoolean, Runnable paramRunnable
                    _launcherShowAllApps = {"showAllApps", "a", "a"},
                    _workspaceMoveToDefaultScreen = {"moveToDefaultScreen", "ao", "at"}, // Launcher onNewIntent method call of workspace member with (true)
                    _btvSetShadowsEnabled = {"setShadowsEnabled", "w", "z"},
                    _wsOverScroll = {"overScroll", "g", "g"}, // (float paramFloat)
                    _acpvOverScroll = {"overScroll", "g", "g"}, // (float paramFloat)
                    _lFinishBindingItems = {"finishBindingItems", "U", "Z"}, // hasFocus()
                    _dpGetWorkspacePadding = {"getWorkspacePadding", "aC", "aS"}, // second method with (int paramInt)
                    _lIsAllAppsVisible = {"isAllAppsVisible", "gs", "hh"}, // onBackPressed first method call
                    _lGetOpenFolder = {"getOpenFolder", "jp", "kj"}, // in closeFolder
                    _wIsOnOrMovingToCustomContent = {"isOnOrMovingToCustomContent", "jJ", "kE"}, // last if-clause in Launcher onResume
                    _wEnterOverviewMode = {"enterOverviewMode", "jO", "kJ"}, // Touchstate != 0
                    _wMoveToCustomContentScreen = {"moveToCustomContentScreen", "ap", "au"}, // Workspace "View localView = getChildAt"
                    _pvSnapToPage = {"snapToPage", "a", "a"}, // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
                    _lOpenFolder = {"openFolder", "i", "i"}, // "Folder info marked as open"
                    _lCloseFolder = {"closeFolder", "gr", "hg"},
                    _acthOnTabChanged = {"onTabChanged", "c", "c"}, // setBackgroundColor
                    _wSetCurrentPage = {"setCurrentPage", "aV", "bl"},
                    _acpvSetCurrentPage = {"setCurrentPage", "aV", "bl"},
                    _dpUpdateFromConfiguration = {"updateFromConfiguration", "a", "a"}, // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
                    _acthSetInsets = {"setInsets", "c", "c"}, // (Rect
                    _wSnapToPage = {"snapToPage", "bc", "bs"}, // in PagedView requestChildFocus
                    _soiSetSearchStarted = {"setSearchStarted", "cs", "cI"}, // onResume before cancel()
                    _noOnShow = {"onShow", "p", "u"}, // boolean paramBoolean1, boolean paramBoolean2
                    _wOnDragEnd = {"onDragEnd", "dt", "ei"}, // only method without interface parameters with InstallShortcutReceiver
                    _wOnDragStart = {"onDragStart", "a", "a"}, // only method with interface parameters with InstallShortcutReceiver
                    _wOnLauncherTransitionEnd = {"onLauncherTransitionEnd", "a", "a"}, // , boolean paramBoolean1, boolean paramBoolean2)
                    _fOnRemove = {"onRemove", "g", "g"}, // removeView(localView)
                    _fOnAdd = {"onAdd", "f", "f"}, // (<Shortcutinfo> param<...>)
                    _fReplaceFolderWithFinalItem = {"replaceFolderWithFinalItem", "ge", "ge"}, // getItemCount() <= 1
                    _fGetItemsInReadingOrder = {"getItemsInReadingOrder", "fr", "gh"}, // public final ArrayList
                    _clGetShortcutsAndWidgets = {"getShortcutsAndWidgets", "dH", "ew"}, // getChildCount() > 0
                    _acthGetContentTypeForTabTag = {"getContentTypeForTabTag", "j", "r"}, // (String paramString)
                    _wOnTransitionPrepare = {"onTransitionPrepare", "jR", "kM"}, // boolean bool = true; this.<IsSwitchingState> = bool;
                    _siGetIntent = {"getIntent", "getIntent", "getIntent"},
                    _icGetFullResIcon = {"getFullResIcon", "a", "a"}, // (Resources paramResources, int paramInt)
                    _uCreateIconBitmap = {"createIconBitmap", "a", "a"}, // (Drawable paramDrawable, Context paramContext)
                    _icCacheLocked = {"cacheLocked", "b", "a"},
                    _clMarkCellsForView = {"markCellsForView", "a", "a"}, // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
                    _lmCheckItemPlacement = {"checkItemPlacement", "a", "a"}, // "Error loading shortcut into "
                    _acpvBeginDragging = {"beginDragging", "n", "n"}, // "instanceof PagedViewIcon" in AppsCustomizePagedView
                    _lBindAppsUpdated = {"bindAppsUpdated", "l", "l"}, // if (this.Hi == null)
                    _lmIsShortcutInfoUpdateable = {"isShortcutInfoUpdateable", "e", "e"}, // "android.intent.action.MAIN"
                    _clAttemptPushInDirection = {"attemptPushInDirection", "b", "b"}, // ArrayList paramArrayList, Rect paramRect, int[] paramArrayOfInt, View paramView, pj parampj
                    _acpvSetApps = {"setApps", "b", "b"}, // Collections.sort
                    _acpvUpdateApps = {"updateApps", "g", "g"}, // in BindAppsUpdated in Launcher
                    _acpvRemoveApps = {"removeApps", "f", "f"}, // in Launcher (paramArrayList2)
                    _lSetWorkspaceBackground = {"setWorkspaceBackground", "N", "S"}, // setBackground
                    _lGetDragLayer = {"getDragLayer", "fV", "gK"}, // public final DragLayer
                    _dlAddResizeFrame = {"addResizeFrame", "a", "a"}, // (-1, -1)
                    _gsaShouldAlwaysShowHotwordHint = {"shouldAlwaysShowHotwordHint", "uK", "xE"}, // always_show_hotword_hint
                    _btvCreateGlowingOutline = {"createGlowingOutline", "a", "a"}, // setBitmap
                    _lmDeleteItemFromDatabase = {"deleteItemFromDatabase", "b", "b"}, // (Context paramContext, ItemInfo paramta) - link to "deleting a folder"
                    _siGetIcon = {"getIcon", "a", "a"}, // public final Bitmap
                    _lmDeleteFolderContentsFromDatabase = {"deleteFolderContentsFromDatabase", "a", "a"}; // (Context paramContext, FolderInfo paramsh)

            applyFromApplicationInfo = _applyFromApplicationInfo[idx];
            lGetApplicationContext = _launcherGetApplicationContext[idx];
            lIsRotationEnabled = _launcherIsRotationEnabled[idx];
            clAddViewToCellLayout = _celllayoutAddViewToCellLayout[idx];
            woiSyncWithScroll = _wallpaperoffsetinterpolatorSyncWithScroll[idx];
            wStartDrag = _workspaceStartDrag[idx];
            acpvOnPackagesUpdated = _acpvOnPackagesUpdated[idx];
            lGetSearchbar = _launcherGetSearchbar[idx];
            lGetQsbBar = _launcherGetQsbBar[idx];
            pvPageBeginMoving = _pagedviewPageBeginMoving[idx];
            pvPageEndMoving = _pagedviewPageEndMoving[idx];
            sdtbOnDragStart = _sdtbOnDragStart[idx];
            sdtbOnDragEnd = _sdtbOnDragEnd[idx];
            lHasCustomContentToLeft = _launcherHasCustomContentToLeft[idx];
            hideAppsCustomizeHelper = _hideAppsCustomizeHelper[idx];
            launcherShowWorkspace = _launcherShowWorkspace[idx];
            launcherShowAllApps = _launcherShowAllApps[idx];
            workspaceMoveToDefaultScreen = _workspaceMoveToDefaultScreen[idx];
            btvSetShadowsEnabled = _btvSetShadowsEnabled[idx];
            wsOverScroll = _wsOverScroll[idx];
            acpvOverScroll = _acpvOverScroll[idx];
            lFinishBindingItems = _lFinishBindingItems[idx];
            dpGetWorkspacePadding = _dpGetWorkspacePadding[idx];
            lIsAllAppsVisible = _lIsAllAppsVisible[idx];
            lGetOpenFolder = _lGetOpenFolder[idx];
            wIsOnOrMovingToCustomContent = _wIsOnOrMovingToCustomContent[idx];
            wEnterOverviewMode = _wEnterOverviewMode[idx];
            wMoveToCustomContentScreen = _wMoveToCustomContentScreen[idx];
            pvSnapToPage = _pvSnapToPage[idx];
            lOpenFolder = _lOpenFolder[idx];
            lCloseFolder = _lCloseFolder[idx];
            acthOnTabChanged = _acthOnTabChanged[idx];
            wSetCurrentPage = _wSetCurrentPage[idx];
            acpvSetCurrentPage = _acpvSetCurrentPage[idx];
            dpUpdateFromConfiguration = _dpUpdateFromConfiguration[idx];
            acthSetInsets = _acthSetInsets[idx];
            wSnapToPage = _wSnapToPage[idx];
            noOnShow = _noOnShow[idx];
            wOnDragEnd = _wOnDragEnd[idx];
            wOnDragStart = _wOnDragStart[idx];
            wOnLauncherTransitionEnd = _wOnLauncherTransitionEnd[idx];
            fOnRemove = _fOnRemove[idx];
            fOnAdd = _fOnAdd[idx];
            fReplaceFolderWithFinalItem = _fReplaceFolderWithFinalItem[idx];
            fGetItemsInReadingOrder = _fGetItemsInReadingOrder[idx];
            clGetShortcutsAndWidgets = _clGetShortcutsAndWidgets[idx];
            soiSetSearchStarted = _soiSetSearchStarted[idx];
            acthGetContentTypeForTabTag = _acthGetContentTypeForTabTag[idx];
            wOnTransitionPrepare = _wOnTransitionPrepare[idx];
            siGetIntent = _siGetIntent[idx];
            icGetFullResIcon = _icGetFullResIcon[idx];
            uCreateIconBitmap = _uCreateIconBitmap[idx];
            icCacheLocked = _icCacheLocked[idx];
            clMarkCellsForView = _clMarkCellsForView[idx];
            lmCheckItemPlacement = _lmCheckItemPlacement[idx];
            acpvBeginDragging = _acpvBeginDragging[idx];
            lBindAppsUpdated = _lBindAppsUpdated[idx];
            lmIsShortcutInfoUpdateable = _lmIsShortcutInfoUpdateable[idx];
            clAttemptPushInDirection = _clAttemptPushInDirection[idx];
            acpvSetApps = _acpvSetApps[idx];
            acpvUpdateApps = _acpvUpdateApps[idx];
            acpvRemoveApps = _acpvRemoveApps[idx];
            lSetWorkspaceBackground = _lSetWorkspaceBackground[idx];
            lGetDragLayer = _lGetDragLayer[idx];
            dlAddResizeFrame = _dlAddResizeFrame[idx];
            gsaShouldAlwaysShowHotwordHint = _gsaShouldAlwaysShowHotwordHint[idx];
            btvCreateGlowingOutline = _btvCreateGlowingOutline[idx];
            lmDeleteItemFromDatabase = _lmDeleteItemFromDatabase[idx];
            lmDeleteFolderContentsFromDatabase = _lmDeleteFolderContentsFromDatabase[idx];
            siGetIcon = _siGetIcon[idx];
        }
    }

    public static class Fields {

        public static String hotseatAllAppsRank,
                dpNumHotseatIcons,
                cllpCanReorder,
                sdtbIsSearchBarHidden,
                sdtbQsbBar,
                wCustomContentShowing,
                wCurrentPage,
                lHotseat,
                lAppsCustomizeTabHost,
                acthInTransition,
                wState,
                wDefaultPage,
                btvShadowsEnabled,
                fiPreviewBackground,
                fiFolderName,
                fFolderEditText,
                fiFolder,
                fiContents,
                fFolderIcon,
                acpvContentType,
                pvIsPageMoving,
                wIsSwitchingState,
                dpHotseatBarHeightPx,
                lState,
                wTouchState,
                pvNextPage,
                lHasFocus,
                lPaused,
                iiItemType,
                aiComponentName,
                acpvCurrentPage,
                acpvAllAppsNumCols,
                acpvAllAppsNumRows,
                pvPageIndicator,
                acthContent,
                dpPageIndicatorHeightPx,
                fContent,
                lAppsCustomizePagedView,
                iiID,
                ceIcon,
                ceTitle,
                lAppWidgetHostView,
                lIconCache,
                iiTitle,
                fiLongPressHelper,
                clphHasPerformedLongPress,
                lawiProviderName,
                fMaxCountY,
                fMaxCountX,
                fMaxNumItems;

        public static void initFieldNames(int idx) {

            String[] _hotseatAllAppsRank = {"hotseatAllAppsRank", "zp", "BQ"}, // only / 2 operation
                    _dpNumHotseatIcons = {"numHotseatIcons", "yz", "AY"}, // toString of DynamicGrid
                    _cllpCanReorder = {"canReorder", "wf", "yE"}, // second member with = true
                    _sdtbIsSearchBarHidden = {"mIsSearchBarHidden", "MV", "PF"}, // above Qsb member
                    _sdtbQsbBar = {"mQSBSearchBar", "MW", "PG"},
                    _wCustomContentShowing = {"mCustomContentShowing", "PV", "SH"}, // setContentDescription
                    _wCurrentPage = {"mCurrentPage", "KF", "Nm"}, // setPadding(0, 0, 0, 0);
                    _acpvCurrentPage = {"mCurrentPage", "KF", "Nm"}, // == _wCurrentPage
                    _lHotseat = {"mHotseat", "EO", "Hu"},
                    _lAppsCustomizeTabHost = {"mAppsCustomizeTabHost", "ER", "Hx"},
                    _acthInTransition = {"mInTransition", "tf", "vF"}, // onInterceptTouchEvent first member in if-clause
                    _wState = {"mState", "Qj", "SV"}, // WorkspaceState member
                    _wDefaultPage = {"mDefaultPage", "PI", "Su"},  // "Expected custom content", member gets decreased by one
                    _btvShadowsEnabled = {"mShadowsEnabled", "ue", "wF"}, // only boolean member = true
                    _fiPreviewBackground = {"mPreviewBackground", "CE", "Fh"}, // only ImageView member
                    _fiFolderName = {"mFolderName", "CF", "Fi"}, // only BubbleTextView
                    _fiFolder = {"mFolder", "CB", "Fe"}, // only Folder member
                    _fiContents = {"contents", "Dt", "FW"}, // first ArrayList in FolderInfo
                    _fFolderIcon = {"mFolderIcon", "BL", "Ep"}, // only FolderIcon member
                    _fFolderEditText = {"mFolderName", "Cf", "EJ"}, // only FolderEditText member
                    _acpvContentType = {"mContentType", "sw", "uW"}, // private oo uW = oo.vW;
                    _pvIsPageMoving = {"mIsPageMoving", "Lv", "Oc"},  // in pageBeginMoving if-clause
                    _dpHotseatBarHeightPx = {"hotseatBarHeightPx", "zo", "BP"}, // 4 * ...
                    _lState = {"mState", "Et", "GZ"}, // onNewIntent if-clause after Folder
                    _wTouchState = {"mTouchState", "KY", "NF"}, // onInterceptTouchEvent while clause
                    _pvNextPage = {"mNextPage", "KI", "Np"}, // String.format
                    _lHasFocus = {"mHasFocus", "Fj", "HP"}, // onWindowFocusChanged
                    _lPaused = {"mPaused", "EZ", "HF"}, // only boolean assignement in onPause()
                    _iiItemType = {"itemType", "En", "GT"}, // Item(id=
                    _aiComponentName = {"componentName", "rJ", "uj"}, // only ComponentName member
                    _acpvAllAppsNumCols = {"allAppsNumCols", "zr", "BS"}, // onMeasure localDeviceProfile
                    _acpvAllAppsNumRows = {"allAppsNumRows", "zq", "BR"}, // onMeasure localDeviceProfile
                    _pvPageIndicator = {"mPageIndicator", "Lz", "Og"}, // setContentDescription
                    _acthContent = {"mContent", "tD", "wd"}, // .getLayoutParams in setInsets
                    _dpPageIndicatorHeightPx = {"pageIndicatorHeightPx", "zw", "BX"}, // last parameter in .set
                    _wIsSwitchingState = {"mIsSwitchingState", "Qk", "SW"}, // start from onTouch, second method call in if-clause
                    _fContent = {"mContent", "BH", "El"}, // only CellLayout member
                    _lAppsCustomizePagedView = {"mAppsCustomizeContent", "ES", "Hy"}, // AppsCustomizePagedView in Launcher
                    _iiID = {"id", "id", "id"},
                    _iiTitle = {"title", "title", "title"},
                    _ceIcon = {"icon", "DZ", "GE"},
                    _ceTitle = {"title", "title", "title"},
                    _lAppWidgetHostView = {"LauncherAppWidgetHostView", "ru", "ve"}, // make logging
                    _lIconCache = {"mIconCache", "rF", "uf"}, // IconCache member in Launcher
                    _fiLongPressHelper = {"mLongPressHelper", "ui", "wJ"}, // cancelLongPress
                    _clphHasPerformedLongPress = {"mHasPerformedLongPress", "wG", "zf"}, // only boolean member
                    _lawiProviderName = {"providerName", "GX", "JF"}, // only ComponentName member
                    _fMaxCountX = {"mMaxCountX", "BM", "Eq"}, // Folder constructor, last line - maxNumItems = X * Y;
                    _fMaxCountY = {"mMaxCountY", "BN", "Er"}, // Folder constructor, last line - maxNumItems = X * Y;
                    _fMaxNumItems = {"mMaxNumItems", "BO", "Es"}; // Folder constructor, last line - maxNumItems = X * Y;

            hotseatAllAppsRank = _hotseatAllAppsRank[idx];
            dpNumHotseatIcons = _dpNumHotseatIcons[idx];
            cllpCanReorder = _cllpCanReorder[idx];
            sdtbIsSearchBarHidden = _sdtbIsSearchBarHidden[idx];
            sdtbQsbBar = _sdtbQsbBar[idx];
            wCustomContentShowing = _wCustomContentShowing[idx];
            wCurrentPage = _wCurrentPage[idx];
            lHotseat = _lHotseat[idx];
            lAppsCustomizeTabHost = _lAppsCustomizeTabHost[idx];
            acthInTransition = _acthInTransition[idx];
            wState = _wState[idx];
            wDefaultPage = _wDefaultPage[idx];
            btvShadowsEnabled = _btvShadowsEnabled[idx];
            fiPreviewBackground = _fiPreviewBackground[idx];
            fiFolderName = _fiFolderName[idx];
            fiFolder = _fiFolder[idx];
            fiContents = _fiContents[idx];
            fFolderIcon = _fFolderIcon[idx];
            fFolderEditText = _fFolderEditText[idx];
            acpvContentType = _acpvContentType[idx];
            pvIsPageMoving = _pvIsPageMoving[idx];
            dpHotseatBarHeightPx = _dpHotseatBarHeightPx[idx];
            lState = _lState[idx];
            wTouchState = _wTouchState[idx];
            pvNextPage = _pvNextPage[idx];
            lHasFocus = _lHasFocus[idx];
            lPaused = _lPaused[idx];
            iiItemType = _iiItemType[idx];
            aiComponentName = _aiComponentName[idx];
            acpvAllAppsNumCols = _acpvAllAppsNumCols[idx];
            acpvAllAppsNumRows = _acpvAllAppsNumRows[idx];
            pvPageIndicator = _pvPageIndicator[idx];
            acthContent = _acthContent[idx];
            dpPageIndicatorHeightPx = _dpPageIndicatorHeightPx[idx];
            wIsSwitchingState = _wIsSwitchingState[idx];
            fContent = _fContent[idx];
            lAppsCustomizePagedView = _lAppsCustomizePagedView[idx];
            iiID = _iiID[idx];
            iiTitle = _iiTitle[idx];
            ceIcon = _ceIcon[idx];
            ceTitle = _ceTitle[idx];
            lAppWidgetHostView = _lAppWidgetHostView[idx];
            lIconCache = _lIconCache[idx];
            fiLongPressHelper = _fiLongPressHelper[idx];
            clphHasPerformedLongPress = _clphHasPerformedLongPress[idx];
            lawiProviderName = _lawiProviderName[idx];
            fMaxCountX = _fMaxCountX[idx];
            fMaxCountY = _fMaxCountY[idx];
            fMaxNumItems = _fMaxNumItems[idx];
            acpvCurrentPage = _acpvCurrentPage[idx];
        }
    }
}