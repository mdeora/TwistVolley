package com.twist.volley;

import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.lang.ref.WeakReference;

/*
 * Copyright (C) 2014 Patrick Doyle
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
public class ImageProperties implements ViewTreeObserver.OnPreDrawListener, ImageLoader.ImageListener {

    private ImageLoader imageLoader;

    public int fade = 0;

    public String url = null;

    public int defaultRes = 0;

    public int errorRes = 0;

    public ImageView.ScaleType scaleType;

    public ImageView.ScaleType preScaleType;

    public WeakReference<ImageView> imageView;

    private ImageLoader.ImageContainer imageContainer;

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = new WeakReference<ImageView>(imageView);
    }

    public void clean() {
        fade = 0;
        defaultRes = 0;
        errorRes = 0;
        url = null;
        imageView = null;
        imageLoader = null;
        scaleType = null;
        preScaleType = null;
    }

    public void cancelRequest() {
        if(imageContainer != null) {
            imageContainer.cancelRequest();
        }
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

        ImageView imageView = this.imageView.get();
        if(response.getBitmap() != null && imageView != null) {
            imageView.setImageBitmap(response.getBitmap());

            if(scaleType != null) {
                imageView.setScaleType(scaleType);
            }

            if(fade != 0) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
                alphaAnimation.setDuration(fade);
                imageView.startAnimation(alphaAnimation);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

        ImageView imageView = this.imageView.get();
        if(imageView != null && errorRes != 0) {
            imageView.setImageResource(errorRes);
        }
    }

    @Override
    public boolean onPreDraw() {

        if(imageView.get() == null) {
            return true;
        }
        imageView.get().getViewTreeObserver().removeOnPreDrawListener(this);

        imageView.get().setImageBitmap(null);
        if(defaultRes != 0) {
            imageView.get().setImageResource(defaultRes);
        }

        if(preScaleType != null) {
            imageView.get().setScaleType(preScaleType);
        }

        imageContainer = imageLoader.get(url, this, imageView.get().getWidth(), imageView.get().getHeight());
        return true;
    }
}
