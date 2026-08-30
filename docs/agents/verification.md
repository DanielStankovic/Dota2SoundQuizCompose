# Verification

How agents check their work in this repo.

## Default: lint only

After code changes, check **Lint / IDE diagnostics** on the files you touched (e.g. ReadLints). That is the default verification.

Do **not** build the project (`./gradlew …`, Assemble, Make Project, install, run on emulator/device) unless the user **explicitly** asks you to. They will say so when they want a build — often for fully autonomous on-device checks of the app itself.

## No automated tests

This project does **not** use automated tests.

Never add, generate, expand, or propose:

- Unit tests
- Instrumented / UI Automator tests
- New files under `app/src/test` or `app/src/androidTest`
- New `*Test.kt` / `*Tests.kt` (or equivalent) test classes

Leave existing example stubs alone unless the user explicitly asks to delete them.

When a skill or design doc talks about a "test surface," "testability," or adapters "for tests," treat that as design vocabulary about the module's interface — **not** as a request to write automated tests.
