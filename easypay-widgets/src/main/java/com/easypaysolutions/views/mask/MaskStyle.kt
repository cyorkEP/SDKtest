package com.easypaysolutions.views.mask

/**
 *
 * Defines the mask visibility and behavior of the cursor
 *
 * [NORMAL]
 * Mask is never visible.
 * Cursor is not limited.
 *
 * [PERSISTENT]
 * Mask becomes visible right after the user started typing and never becomes hidden.
 * Cursor is limited between mask characters.
 * Placeholders are not allowed to delete.
 */
internal enum class MaskStyle {
    NORMAL,
    PERSISTENT
}
