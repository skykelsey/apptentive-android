/*
 * Copyright (c) 2014, Apptentive, Inc. All Rights Reserved.
 * Please refer to the LICENSE file for the terms and conditions
 * under which redistribution and use of this file is permitted.
 */

package com.apptentive.android.sdk.module.rating;

import android.content.Context;
import com.apptentive.android.sdk.module.rating.impl.AmazonAppstoreRatingProvider;
import com.apptentive.android.sdk.module.rating.impl.GooglePlayRatingProvider;
import com.apptentive.android.sdk.module.rating.impl.MiKandiMarketRatingProvider;
import com.apptentive.android.sdk.module.rating.impl.NokiaXStoreRatingProvider;
import com.apptentive.android.sdk.util.Util;

/**
 * @author SKy Kelsey
 */
public class RatingProviderFactory {

	public IRatingProvider getAppropriateRatingProvider(Context context) {
		String installerPackageNameString = Util.getInstallerPackageName(context);
		InstallerPackageName installerPackageName = InstallerPackageName.findByInstallerPackagename(installerPackageNameString);
		switch (installerPackageName) {
			case google:
				return new GooglePlayRatingProvider();
			case amazon:
				return new AmazonAppstoreRatingProvider();
			case nokia:
				return new NokiaXStoreRatingProvider();
			case mikandi:
				return new MiKandiMarketRatingProvider(1234567890); // TODO: Let's see if we can remove this parameter from the constructor...
			case unknown:
			default:
				return new GooglePlayRatingProvider();
		}
	}

	private static enum InstallerPackageName {
		google("com.android.vending"),
		nokia("com.nokia.nstore"),
		amazon("com.amazon.venezia"),
		mikandi("com.mikandi.jvending"),
		unknown("unknown");

		private String installerPackageNameString;

		private InstallerPackageName(String installerPackageNameString) {
			this.installerPackageNameString = installerPackageNameString;
		}

		private String getInstallerPackageName() {
			return this.installerPackageNameString;
		}

		public static InstallerPackageName findByInstallerPackagename(String installerPackageNameString) {
			if (installerPackageNameString != null) {
				for (InstallerPackageName installerPackageName : InstallerPackageName.values()) {
					if (installerPackageNameString.equals(installerPackageName.getInstallerPackageName())) {
						return installerPackageName;
					}
				}
			}
			return unknown;
		}
	}
}