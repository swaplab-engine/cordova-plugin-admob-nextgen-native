package com.google.android.ads.nativetemplates;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.Nullable;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public class NativeTemplateStyle {

  private Typeface callToActionTextTypeface;

  private float callToActionTextSize;

  @Nullable private Integer callToActionTypefaceColor;

  private ColorDrawable callToActionBackgroundColor;

  private Typeface primaryTextTypeface;

  private float primaryTextSize;

  @Nullable private Integer primaryTextTypefaceColor;

  private ColorDrawable primaryTextBackgroundColor;

  private Typeface secondaryTextTypeface;

  private float secondaryTextSize;

  @Nullable private Integer secondaryTextTypefaceColor;

  private ColorDrawable secondaryTextBackgroundColor;

  private Typeface tertiaryTextTypeface;

  private float tertiaryTextSize;

  @Nullable private Integer tertiaryTextTypefaceColor;

  private ColorDrawable tertiaryTextBackgroundColor;

  private ColorDrawable mainBackgroundColor;

  public Typeface getCallToActionTextTypeface() {
    return callToActionTextTypeface;
  }

  public float getCallToActionTextSize() {
    return callToActionTextSize;
  }

  @Nullable
  public Integer getCallToActionTypefaceColor() {
    return callToActionTypefaceColor;
  }

  public ColorDrawable getCallToActionBackgroundColor() {
    return callToActionBackgroundColor;
  }

  public Typeface getPrimaryTextTypeface() {
    return primaryTextTypeface;
  }

  public float getPrimaryTextSize() {
    return primaryTextSize;
  }

  @Nullable
  public Integer getPrimaryTextTypefaceColor() {
    return primaryTextTypefaceColor;
  }

  public ColorDrawable getPrimaryTextBackgroundColor() {
    return primaryTextBackgroundColor;
  }

  public Typeface getSecondaryTextTypeface() {
    return secondaryTextTypeface;
  }

  public float getSecondaryTextSize() {
    return secondaryTextSize;
  }

  @Nullable
  public Integer getSecondaryTextTypefaceColor() {
    return secondaryTextTypefaceColor;
  }

  public ColorDrawable getSecondaryTextBackgroundColor() {
    return secondaryTextBackgroundColor;
  }

  public Typeface getTertiaryTextTypeface() {
    return tertiaryTextTypeface;
  }

  public float getTertiaryTextSize() {
    return tertiaryTextSize;
  }

  @Nullable
  public Integer getTertiaryTextTypefaceColor() {
    return tertiaryTextTypefaceColor;
  }

  public ColorDrawable getTertiaryTextBackgroundColor() {
    return tertiaryTextBackgroundColor;
  }

  public ColorDrawable getMainBackgroundColor() {
    return mainBackgroundColor;
  }

  public static class Builder {

    private NativeTemplateStyle styles;

    public Builder() {
      this.styles = new NativeTemplateStyle();
    }

    @CanIgnoreReturnValue
    public Builder withCallToActionTextTypeface(Typeface callToActionTextTypeface) {
      this.styles.callToActionTextTypeface = callToActionTextTypeface;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withCallToActionTextSize(float callToActionTextSize) {
      this.styles.callToActionTextSize = callToActionTextSize;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withCallToActionTypefaceColor(int callToActionTypefaceColor) {
      this.styles.callToActionTypefaceColor = callToActionTypefaceColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withCallToActionBackgroundColor(ColorDrawable callToActionBackgroundColor) {
      this.styles.callToActionBackgroundColor = callToActionBackgroundColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withPrimaryTextTypeface(Typeface primaryTextTypeface) {
      this.styles.primaryTextTypeface = primaryTextTypeface;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withPrimaryTextSize(float primaryTextSize) {
      this.styles.primaryTextSize = primaryTextSize;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withPrimaryTextTypefaceColor(int primaryTextTypefaceColor) {
      this.styles.primaryTextTypefaceColor = primaryTextTypefaceColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withPrimaryTextBackgroundColor(ColorDrawable primaryTextBackgroundColor) {
      this.styles.primaryTextBackgroundColor = primaryTextBackgroundColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withSecondaryTextTypeface(Typeface secondaryTextTypeface) {
      this.styles.secondaryTextTypeface = secondaryTextTypeface;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withSecondaryTextSize(float secondaryTextSize) {
      this.styles.secondaryTextSize = secondaryTextSize;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withSecondaryTextTypefaceColor(int secondaryTextTypefaceColor) {
      this.styles.secondaryTextTypefaceColor = secondaryTextTypefaceColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withSecondaryTextBackgroundColor(ColorDrawable secondaryTextBackgroundColor) {
      this.styles.secondaryTextBackgroundColor = secondaryTextBackgroundColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withTertiaryTextTypeface(Typeface tertiaryTextTypeface) {
      this.styles.tertiaryTextTypeface = tertiaryTextTypeface;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withTertiaryTextSize(float tertiaryTextSize) {
      this.styles.tertiaryTextSize = tertiaryTextSize;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withTertiaryTextTypefaceColor(int tertiaryTextTypefaceColor) {
      this.styles.tertiaryTextTypefaceColor = tertiaryTextTypefaceColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withTertiaryTextBackgroundColor(ColorDrawable tertiaryTextBackgroundColor) {
      this.styles.tertiaryTextBackgroundColor = tertiaryTextBackgroundColor;
      return this;
    }

    @CanIgnoreReturnValue
    public Builder withMainBackgroundColor(ColorDrawable mainBackgroundColor) {
      this.styles.mainBackgroundColor = mainBackgroundColor;
      return this;
    }

    public NativeTemplateStyle build() {
      return styles;
    }
  }
}
