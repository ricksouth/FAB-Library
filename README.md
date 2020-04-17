## The Find A Block Library assists in finding a specific block and keeping track of its location in a world efficiently.

For a more detailed description of the library mod visit the [FAB Library Project Page](https://www.curseforge.com/minecraft/mc-mods/fab-library).\
For more information on the public functions the mod adds, see the [Wiki](https://github.com/ricksouth/FAB-Library/wiki).

# How to use the library in your project:
### 1. Add the following lines to your build.gradle file:

repositories {\
    maven {\
        url = "https://github.com/ricksouth/serilum-forge-maven/raw/maven/" \
    }\
}

dependencies {\
    runtimeOnly fg.deobf("com.natamus.fablibrary:fablibrary:VERSION")\
    compileOnly fg.deobf("com.natamus.fablibrary:fablibrary:VERSION")\
}

<br/><br/>

### 2. Replace VERSION in the previous lines with mcversion-modversion, e.g. 1.14.4-1.0, or 1.15.2-1.0:
dependencies {\
    runtimeOnly fg.deobf("com.natamus.fablibrary:fablibrary:1.15.2-1.0")

<br/><br/>

### 3. After that refresh your workspace by entering the following command in your project folder:
$ gradlew cleanEclipse eclipse --refresh-dependencies

<br/><br/>

### 4. Add the following lines to the bottom of mods.toml in your META-INF folder, replace YOURMODID with your mod's id:
[[dependencies.YOURMODID]]\
    modId="fablibrary" \
    mandatory=true \
    versionRange="[1.0,)" \
    ordering="NONE" \
    side="BOTH"

<br/><br/>

### 5. When uploading the file to your mod project page on curseforge, make sure to select FAB Library as a dependent at the bottom:

![project_dependent](https://i.imgur.com/6xS9SCK.png)
