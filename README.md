# How to use the library in your project:
### 1. Add the following lines in your build.gradle file:

repositories {\
    maven {\
        url = "https://github.com/ricksouth/serilum-forge-maven/raw/maven/" \
    }\
}

dependencies {\
    runtimeOnly fg.deobf("com.natamus.fablibrary:fablibrary:VERSION")\
    compileOnly fg.deobf("com.natamus.fablibrary:fablibrary:VERSION")\
}




### 2. Replace VERSION in the previous lines with mcversion-modversion, e.g. 1.14.4-1.0, or 1.15.2-1.0:
dependencies {\
    runtimeOnly fg.deobf("com.natamus.fablibrary:fablibrary:1.15.2-1.0")



### 3. After that refresh your workspace by entering the following command in your project folder:
$ gradlew cleanEclipse eclipse --refresh-dependencies



### 4. Add the following lines to the bottom of mods.toml in your META-INF folder:
[[dependencies.YOURMODID]]\
    modId="fablibrary" \
    mandatory=true \
    versionRange="[1.0,)" \
    ordering="NONE" \
    side="BOTH"



### 5. When uploading the file to your mod project page on curseforge, make sure to select FAB Library as a dependent at the bottom:

![project_dependent](https://i.imgur.com/6xS9SCK.png)
