# Building the iDempiere ZK EE Components Example Plugin
This document provides a step-by-step guide on how to build the `org.idempiere.zkee.comps.example` plugin from the `zkoss-idempiere-examples` repository.

For general iDempiere plugin development guidelines, refer to the [iDempiere Wiki](https://wiki.idempiere.org/en/Developing_Plug-Ins_-_Get_your_Plug-In_running).

## Introduction

Building iDempiere plugins requires having the iDempiere core libraries available as a local p2 repository. This guide will walk you through the process of setting up the necessary dependencies and building the plugin.

## Prerequisites

Before you begin, ensure you have the following tools installed:

-   **Git:** For cloning the iDempiere repository.
-   **Maven:** For building the projects.
-   **Java Development Kit (JDK):** Version 17 or higher.
-   **iDempiere Runtime**: An active instance (e.g., [Official Docker Image](https://hub.docker.com/r/idempiereofficial/idempiere)).

## Step-by-step Guide

### 1. Clone iDempiere Core

The first step is to clone the main iDempiere project, which provides the core libraries needed to build the plugins.

```bash
# Clone version 12 of the iDempiere project
git clone --branch release-12 https://github.com/idempiere/idempiere.git idempiere-12
```

This will create a directory named `idempiere-12` containing the iDempiere source code.

Make the core and `zkoss-idempiere-examples` sibling directories.

### 2. Build iDempiere Core

This creates a local p2 repository at `idempiere-12/org.idempiere.p2/target/repository`.

```bash
cd idempiere-12
mvn clean install
```

### 3. Point the examples to your absolute core paths
- Adjust the target platform files to absolute paths:
  - `org.idempiere.p2.targetplatform/org.idempiere.p2.targetplatform.target`
  - `org.idempiere.p2.targetplatform/org.idempiere.p2.targetplatform.mirror.target`
  Change `${project_loc:org.idempiere.p2.targetplatform}` to your absolute `.../idempiere/org.idempiere.p2.targetplatform`.

### 4. Add a ZK PE/EE fragment

Since we want the web UI to load ZK PE/EE widgets (e.g., from zkex and zkmax), use the fragment project `org.idempiere.zkee.comps.fragment`:

1) Build the fragment in the core repo (it lives at `idempiere/org.idempiere.zkee.comps.fragment`):
```bash
cd zkoss-idempiere-examples/org.idempiere.zkee.comps.fragment
mvn -U -DskipTests -am verify
```
   This runs the dependency-copy step and produces `org.idempiere.zkee.comps.fragment/target/org.idempiere.zkee.comps.fragment-<version>.jar`.
2) Install the fragment into your OSGi runtime (for example via Felix Web Console, or by placing the jar in the plugins directory) and restart the server so the host bundle (`org.adempiere.ui.zk`) resolves with the fragment on its classpath.
3) Confirm the fragment is active; the ZK PE/EE widgets (defined in the embedded `zkex`/`zkmax` lang-addons) should render without “widget class required” errors.

#### What is in `org.idempiere.zkee.comps.fragment`?

- Purpose: OSGi fragment that attaches both ZK PE and EE jars to `org.adempiere.ui.zk`, exposing lang-addons, widgets, and resources from zkex/zkmax plus `gson` for supporting components.
- Key files:
  - `META-INF/MANIFEST.MF`: `Fragment-Host: org.adempiere.ui.zk`, `Bundle-ClassPath: ., lib/zkex.jar, lib/zkmax.jar, lib/gson.jar`.
  - `build.properties`: includes `META-INF/`, `lib/zkex.jar`, `lib/zkmax.jar`, `lib/gson.jar` so they are packaged inside the fragment.
  - `pom.xml`: eclipse-plugin packaging; EE eval repository; dependency-copy execution to fetch `zkex`, `zkmax`, and `gson` into `lib/` (version-stripped).
  - `src/metainfo/zk.xml`: sets EE-specific library property (`org.zkoss.zkmax.au.IWBS.disable=true`).
  - `lib/zkex.jar`, `lib/gson.jar`, `lib/zkmax.jar`: ZK PE/EE binaries with `metainfo/zk/lang-addon.xml` and widget resources.
  - `target/`: built outputs (`org.idempiere.zkee.comps.fragment-<version>.jar`, generated manifest, p2 metadata).

### 5. Use ZK EE components in your own plugin (e.g., `org.idempiere.zkee.comps.example`)
 
1) Ensure the ZK EE fragment (`org.idempiere.zkee.comps.fragment`) is installed and active in the runtime; restart the server so `org.adempiere.ui.zk` resolves with the fragment on its classpath.
2) If your build cannot see the EE jar, add a dependency-copy step similar to the fragment (pulling the EE jar into `lib/`) or add the EE bundle to your target platform so Tycho can resolve it.
3) In ZUL, once the fragment is active, you can directly use EE components (e.g., `<timepicker .../>`) because the lang-addon from the fragment registers them. See `org.idempiere.zkee.comps.example/src/web/form.zul` for an example using EE widgets.
4) Build your plugin and redeploy; verify there are no “Widget class required…” errors and the EE components render.
```bash
cd zkoss-idempiere-examples/org.idempiere.zkee.comps.example
mvn clean verify
```
Artifacts are written to `target/`.