# 3. Simple Interface for Driving Adapters

Date: 2021-12-23
## Status

Accepted

## Context
A key requirement for durable software systems is the ability to incorporate technology stacks that may not have existed when the application was first developed. In this context, "new" refers not only to entirely different technologies but also to newer versions of existing stacks that introduce breaking changes. Often, providing a fresh implementation is more sustainable than refactoring legacy integrations to fit new version requirements.

## Decision
We will leverage Jexxa’s minimalist API, which allows for the integration of arbitrary technology stacks as driving adapters. Combined with the capability for explicit binding at the object level, we support the following strategic use cases:

* **Technology Evaluation:** Students can evaluate and integrate new technology stacks as part of bachelor or master theses without impacting the core system.

* **Incremental Migration:** Binding driving adapters at the object level enables the integration, updating, and migration of specific technology stacks in the smallest possible steps.

* **Legacy Coexistence:** New versions with breaking changes can be implemented as separate adapters, allowing for a side-by-side transition.

## Consequences

* Positive: High flexibility for future technology shifts and reduced technical debt during upgrades.

* Positive: Lower barrier for innovation and academic collaboration.

* Positive: Risk mitigation through granular, step-by-step migration paths.

* Negative: Managing multiple adapter implementations simultaneously for a short period may increase architectural complexity.