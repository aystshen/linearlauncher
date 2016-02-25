/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package android.support.v17.leanback.widget;

import android.os.Build;
import android.view.ViewGroup;
import android.view.View;


/**
 * Helper for shadow.
 */
final class ShadowHelper {

    final static ShadowHelper sInstance = new ShadowHelper();
    boolean mSupportsShadow;
    boolean mUsesZShadow;
    ShadowHelperVersionImpl mImpl;

    /**
     * Interface implemented by classes that support Shadow.
     */
    static interface ShadowHelperVersionImpl {

        public void prepareParent(ViewGroup parent);

        public Object addShadow(ViewGroup shadowContainer, boolean roundedCorners);

        public void setZ(View view, float z);

        public void setShadowFocusLevel(Object impl, float level);

    }

    /**
     * Interface used when we do not support Shadow animations.
     */
    private static final class ShadowHelperStubImpl implements ShadowHelperVersionImpl {

        @Override
        public void prepareParent(ViewGroup parent) {
            // do nothing
        }

        @Override
        public Object addShadow(ViewGroup shadowContainer, boolean roundedCorners) {
            // do nothing
            return null;
        }

        @Override
        public void setShadowFocusLevel(Object impl, float level) {
            // do nothing
        }

        @Override
        public void setZ(View view, float z) {
            // do nothing
        }

    }

    /**
     * Returns the ShadowHelper.
     */
    private ShadowHelper() {
        mSupportsShadow = false;
        mImpl = new ShadowHelperStubImpl();
    }

    public static ShadowHelper getInstance() {
        return sInstance;
    }

    public boolean supportsShadow() {
        return mSupportsShadow;
    }

    public boolean usesZShadow() {
        return mUsesZShadow;
    }

    public void prepareParent(ViewGroup parent) {
        mImpl.prepareParent(parent);
    }

    public Object addShadow(ViewGroup shadowContainer, boolean roundedCorners) {
        return mImpl.addShadow(shadowContainer, roundedCorners);
    }

    public void setShadowFocusLevel(Object impl, float level) {
        mImpl.setShadowFocusLevel(impl, level);
    }

    /**
     * Set the view z coordinate.
     */
    public void setZ(View view, float z) {
        mImpl.setZ(view, z);
    }

}
