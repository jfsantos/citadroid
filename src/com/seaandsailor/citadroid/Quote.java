/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seaandsailor.citadroid;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for NotePadProvider
 */
public final class Quote {
    public static final String AUTHORITY = "com.seaandsailor.provider.CitaDroid";

    // This class cannot be instantiated
    private Quote() {}
    
    /**
     * Quotes table
     */
    public static final class Quotes implements BaseColumns {
        // This class cannot be instantiated
        private Quotes() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/quotes");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of quotes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.note";

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.note";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The author of the quote
         * <P>Type: TEXT</P>
         */
        public static final String AUTHOR = "author";

        /**
         * The quote itself
         * <P>Type: TEXT</P>
         */
        public static final String QUOTE = "quote";

    }
}
