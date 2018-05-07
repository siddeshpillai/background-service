#-------------------------------------------------------------------------------
# Copyright 2015 Esri
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#-------------------------------------------------------------------------------

QT += androidextras

INCLUDEPATH += $$PWD
DEPENDPATH += $$PWD

ANDROID_PACKAGE_SOURCE_DIR = $$PWD

SOURCES += \
    $$PWD/QmlApplicationJNI.cpp


OTHER_FILES += \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/QmlApplicationActivity.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/NotificationPermissionUtils.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/NotificationReceiver.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/FingerprintAuthenticationDialogFragment.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/FingerprintUiHelper.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/SecureStorageCallback.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/LocationWakefulReceiver.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/src/com/arcgis/appframework/LocationIntentService.java \
    $$ANDROID_PACKAGE_SOURCE_DIR/AndroidManifest.xml \
    $$ANDROID_PACKAGE_SOURCE_DIR/gradle/wrapper/gradle-wrapper.jar \
    $$ANDROID_PACKAGE_SOURCE_DIR/gradle/wrapper/gradle-wrapper.properties \
    $$ANDROID_PACKAGE_SOURCE_DIR/gradlew \
    $$ANDROID_PACKAGE_SOURCE_DIR/build.gradle \
    $$ANDROID_PACKAGE_SOURCE_DIR/gradlew.bat \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/values/libs.xml \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/values/strings.xml \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable/ic_fingerprint_success.xml \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable/ic_fingerprint_error.xml \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-ldpi/icon.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-mdpi/icon.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-hdpi/icon.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-xhdpi/icon.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-xxhdpi/icon.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-ldpi/splash.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-mdpi/splash.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-hdpi/splash.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-xhdpi/splash.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-xxhdpi/splash.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-hdpi/ic_fingerprint.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-mdpi/ic_fingerprint.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-xhdpi/ic_fingerprint.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/drawable-xxhdpi/ic_fingerprint.png \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/layout/fingerprint_dialog_container.xml \
    $$ANDROID_PACKAGE_SOURCE_DIR/res/layout/fingerprint_dialog_content.xml

#----------------------------------------------------------------------

qtPrepareTool(QDOC, qdoc)
QTDIR=$$absolute_path("", $$QDOC/../..)

#----------------------------------------------------------------------

exists( $$QTDIR/static ) {
    message("Excluding Static Libs: " $${ignoreLibs})
    for (f, $$list($$files($${QTDIR}/static/lib*.a))) {
        lib=start-$$basename(f)-end
        lib=$$replace(lib, start-lib, )
        lib=$$replace(lib, .a-end, )

        !contains(ignoreLibs, $${lib}) {
            message( "Linking Static Libs " $${lib} )

            !use_static_plugins {
                CONFIG += use_static_plugins
                LIBS += -L$$QTDIR/static
                DEFINES += USE_STATIC_PLUGINS
            }

            LIBS += -l$${lib}
            QTPLUGIN += $${lib}
            DEFINES += USE_STATIC_PLUGIN_$${lib}
        } else {
            message( "Skipping Static Plugin " $${lib} )
        }
    }
}

#-------------------------------------------------------------------------------

DISTFILES += \
    $$PWD/src/com/arcgis/appframework/LocationJobIntentService.java
