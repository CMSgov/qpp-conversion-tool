* [Design Exercise Features](#design-exercise-features)
* [Technologies](#technologies)
* [Our Agile Dev Process](#our-agile-dev-process)
* [Design Process](#design-process)
* [User Instructions](#user-instructions)
* [Hypothetical Next Steps](#hypothetical-next-steps)
* [Developer Installation Instructions](#developer-installation-instructions)
* [Development Tasks](#development-tasks)

## Design Exercise Features

* **Persona Development** - [One persona](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/documents/QPP%20Conversion%20Tool%20Persona%20-%20Joseph.pdf) and a hpyothetical Product Owner were used to used to drive [epics and user stories](https://github.com/flexion/adele-bpa-beneficiary-reporting/blob/develop/documents/design-deliverables/kickoff/user-stories-whiteboard-exercise.jpg). The external behavior of the tool was driven by the persona, priorities of QRDA-III conversion were driven by the Product Owner.
* **Decoupled and highly testable design** - We decoupled decoding the XML, validating the relevant content, and encoding the JSON along `templateId` lines. This enabled fine-grained development and automated testing of decoding, validating, and encoding functions. Each of these for a given `templateId` were accepted by different developers, allowing the team to effectively collaborate on of one `templateId` at a time.
* **Automated functional tests** - [90% plus code coverage](<link-to-screenshot-of circle-code-coverage>). (See [User Instructions: Test Coverage and Reporting in CircleCI](#test-coverage-and-reporting-in-circleci) for a guide to viewing live reports.)
* **End-to-End tests** - Automated [End-to-End Coverage](<link-to-file-level-tests>).
* **Code review process** - Using GitHub's [PR and code review](<link-to-completed-PRs>) for merges from feature branches to main
* **Continuous Integration** - CircleCI performing [Full Continuous Integration](<link to circleCI build history screen>).

### Where we would raise the bar in the real project

Here are the areas where we would go further in the actual project than in the design exercise.

* More thourough validation - We implemented two `templateId` validators to dempnstrate how validation would be invoked, and how validation messages would be presented to users.
* Continuous user feedback - We would have our users part of the daily process, working with the file converter to provide continuous feedback.

## Technologies

Our chosen technologies include:

* Java 8 - <https://angular.io/>
* Maven 3 - <https://github.com/angular/angular-cli>
* CircleCI - <https://circleci.com>

## Our Agile Dev Process

![Flexion Agile Dev Process](https://github.com/flexion/adele-bpa-beneficiary-reporting/blob/develop/documents/design-deliverables/diagrams/Agile_Dev_Methodology_Diagram.jpg)

## Design Process

Below are the steps we would normally take to develop a user story. Because this is a demo constructed in a compressed time frame by a partial team, some of the steps are "stubs," listed so you can see where they fall in the process. Links will take you to the artifacts that are associated with the step in the process.

Each story goes through all of these process steps. The number of user stories that go through the cycle together is a function of the maturity of the Scrum team. As we improve our processes, the size of each "batch" decreases while the value delivered each sprint increases.

### 1. Identify persona and epic or user story.

The purpose of creating personas and their associated epics and user stories is simply to better understand the design problems that need to be solved and who we are solving them for.

1. **Name and describe the personas [Joseph](<link to Joseph>) and [Name](<link to name>).**

1.  **Tell the user story.** At this point, we recommend that the PO focus on business-level stories so that option space of system interactions (to achieve the business goal) is as large as possible. The UX design process explores this space of options. Here is the user story we are assuming for this exercise:

    > As a Perfomance Improvement IT Analyst at a large hospital, in order to submit measures to CMS, I want to convert a number of QRDS-III files produced by our existing sofware systems into the new QPP format.

### 2. Refine the Product Backlog.

The purpose of refining the backlog is to subsume new PO goals into the backlog with just enough detail to be able to prioritize it.

1. **The PO explains the persona and tells the user story.**

1. **Team asks clarifying questions.** If the team learns that the story is big, they "promote" it to an "epic".

1.  **They negotiate and converge on draft acceptance criteria.** Acceptance criteria are the business conditions that must be true when the story is successfully completed. Detailed user interactions are not included but emerge during the development process.

    > Acceptance Criteria:
    >
    > Given that there are a number of QRDA-III files in a given directory, the IT Analyst can convert them all to the QPP format using a single, simple command.

4.  **The team brainstorms options for meeting the goal.** Options can be mutually exclusive, or independent features that contribute to the overall goal.

5.  **The team estimates the value-flow rate relative to other user stories.** Two components are inputs to value-flow rate: relative cost of delay (estimated by business) and relative duration (estimated by Scrum team).

    In the design exercise, the user stories were trimmed to five and ranked as follows:

    > As a Perfomance Improvement IT Analyst, in order to convert an ACI "proportion" measure, I want to fetch Aggregate Count value.
    >
    > As a Perfomance Improvement IT Analyst, in order to convert an ACI "proportion" measure, I want to convert an ACI numerator measure.
    >
    > As a Perfomance Improvement IT Analyst, in order to convert the ACI section, I want to convert an ACI proportion measure.
    >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want multiple people to enter data concurrently by allied measures.
    >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want to filter inapplicable measures.
    >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want to filter inapplicable measures.
    >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want to filter inapplicable measures.
        >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want to filter inapplicable measures.
        >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want to filter inapplicable measures.

### 3. Plan the Sprint.

The purpose of Sprint Planning is to pull enough work into the next upcoming sprint to keep the team allocated but as focused as possible in their current state. The team drafts a plan for how to organize the work in the sprint. The granularity of atomic user stories for the Product Owner will likely require that they span multiple sprints, especially when UX research, testing, and deployment are included. So sprint planning is also where atomic PO user stories might be decomposed into smaller demonstrable user stories that can be ingested into the sprint.

1. **The PO introduces upcoming stories in value-flow rate order.**

1.  **The team reviews the story with the PO.** Acceptance criteria and story point estimates are adjusted as needed. (We used [GitHub projects as a proxy for a story card](<link to sorted GitHub projects) for the purposes of this design exercise.)

1.  **The team accepts stories into the sprint**. This decision is based on measured velocity and estimated story points. If the next story is too large to fit into the sprint, the team divides the acceptance criteria into pieces and writes new stories based on those. This repeats until a story can be accepted.

    In the design exercise, accepted the following two into the sprint:

    > As a Perfomance Improvement IT Analyst, in order to convert an ACI "proportion" measure, I want to fetch Aggregate Count value.
    >
    > As a Perfomance Improvement IT Analyst, in order to convert an ACI "proportion" measure, I want to convert an ACI numerator measure.
    >
    > As a Perfomance Improvement IT Analyst, in order to convert the ACI section, I want to convert an ACI proportion measure.
    >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want multiple people to enter data concurrently by allied measures.
    >
    > As a large practice manager, in order to enter a lot of quality measures quickly, I want to filter inapplicable measures.
    >


1. **The team crafts a Sprint Goal.** The sprint goal is a unifying goal that binds together all user stories accepted into the sprint.

1.  **The Scrum team drafts a [cross-functional task list] required to get the story to done.** The list includes *all* tasks from UX research to coding to testing to deployment. These tasks are just abstract enough to avoid becoming obsolete once related details of the work start to take shape.

    In the design exercise, we use GitHub projects to hold our tasks. Click on the GitHub project's title to drill down into the task list. Here is one example:

    [As a Perfomance Improvement IT Analyst, in order to convert an ACI "proportion" measure, I want to fetch Aggregate Count value.](<link to this prjects task in github>)

### 4. Execute the Sprint.

This is the bulk of the two-week sprint where the development team develops. This includes identifying and evaluating options, developing them, testing them, and completing all tasks in the Definition of Done (DoD). This process isn't linear but can go through multiple cycles before it's done. Also, although these can be thought of as identical cycles, the order at which these things can happen is fluid and emerges based on need.

1.  **Create automated ATDD/BDD tests from the static click-through and the business model.** These tests drive production development and are integrated into the CI/CD pipeline.

    We have automated tests to drive development at multiple levels. Within the ["java-conversion-tool/src/test/java/gov/cms/qpp" directory](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/java-conversion-tool/src/test/java/gov/cms/qpp)

1.  **The development team collaborates to fully build out the user story and its supporting unit tests.** Implementation decouples the sofware components greatest extent possible.

    Our clean architecture allows us to [develop locally](https://github.com/flexion/adele-bpa-beneficiary-reporting/blob/develop/documents/clean-architecture/Local-Dev-Diagram.jpg) with high efficiency, as everything runs in process.
    
    The clean separation at the boundary between business logic and it's plugins, including the UI and database, give us deployment options that we can exercise depending on the circumstances. We can move the stateless [business logic to the server](https://github.com/flexion/adele-bpa-beneficiary-reporting/blob/develop/documents/clean-architecture/Remote-App-Server-DevProd-Diagram.jpg), leaving only the pure UI in the browser. Alternatively, we can leave the business logic in the browser and move only the [datastore to the server](https://github.com/flexion/adele-bpa-beneficiary-reporting/blob/develop/documents/clean-architecture/Remote-DB-DevProd-Diagram.jpg). Or we could do both.

    In either case, a REST component is inserted at the boundary that neither the UI or business component is aware of. It completely encapsulates all security, configuration, and security to establish reliable and secure communication between browser and the server infrastructure. For the design exercise, we opted for the local model to accelerate development cycles.

1. **The user story is validated**. Validation is accomplished by testing with real people who have been identified as likely prospective users of the system.

1. **Recommendations from the user testing are placed into the project backlog for implementation in the application.**

1.  **The team achieves all items in the story’s DoD Checklist.**

    Our Definition of Done includes the following:

    * Acceptance criteria met (UAT)
    * Code refactored for clarity
    * Framework code decoupled from ours
    * Source code committed.
    * Unit test coverage of our code = 100%
    * Code reviewed (or developed by pair)
    * Security reviewed and reported
    * No local design debt
    * Build process updated
    * Feature toggles enabled
    * Source documentation generated
    * API documentation generated
    * Code quality checks passed
    * Load tests passed
    * Guides and training draft updated
    * Security & privacy gates passed

### 5. Review the Sprint

The Sprint Review is the accountability ceremony, where the team demos anything that meets the Definition of Done by the time this meeting takes place, or explains why things they expected to complete weren't. The PO accepts or rejects their work. The team also discusses why stories that they expected to complete aren't complete yet. (Hopefully, there have been enough regular interactions with the PO that the team already knows that the completed story will be accepted.)

1. **The team demos the story.** The demo covers the acceptance criteria for the story and only features stories that achieve an adequate and agreed-upon level of "doneness" as defined in the Definition of Done.

1. **Hand over system artifacts.** In this case, all artifacts are committed into the GitHub repository, which the PO and government have access to.

1. **A working version of the system is accessible to the PO.**




#### CircleCI Report Examples ####

* **Automated functional tests** - [90% plus code coverage](https://329-79416044-gh.circle-artifacts.com/0/tmp/circle-artifacts.XhwiBNU/coverage/index.html).
* **End-to-End tests** - Automated [end-to-end tests](https://circleci.com/gh/flexion/adele-bpa-beneficiary-reporting/329) - *scroll to 'npm run e2e' to verify that everything is wired up.*
* **Automated accessibility tests** - [Pa11y tests](https://circleci.com/gh/flexion/adele-bpa-beneficiary-reporting/329) - *scroll to 'npm run pa11y.'*
* **Continuous Integration** - CircleCI performing [full continuous integration](https://circleci.com/gh/flexion/adele-bpa-beneficiary-reporting/329) and requiring a high bar to pass.
* **Continuous Deployment** - Bundle [Docker image and deploy on AWS](http://adele-bpa-beneficiary-reporting.shareddev.flexion.us/) gold image after [every successful build](https://circleci.com/gh/flexion/adele-bpa-beneficiary-reporting/329) - *scroll to 'Deployment.'*