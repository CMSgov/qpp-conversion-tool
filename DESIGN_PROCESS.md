* [Design Exercise Features](#design-exercise-features)
* [Technologies](#technologies)
* [Design Process](#design-process)
* [Test Coverage and Reporting in CircleCI](#test-coverage-and-reporting-in-circleci)
* [Things we will address in the next sprint](#things-we-will-address-in-the-next-sprint)

## Design Exercise Features

* **Persona Development** - [A primary user persona](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/documents/QPP%20Conversion%20Tool%20Persona%20-%20Joseph.pdf) and a hypothetical Product Owner were used to used to drive epics and user stories. The external behavior of the tool was driven by the persona, priorities of QRDA-III conversion were driven by the Product Owner.
* **Decoupled and highly testable design** - We decoupled decoding the XML, validating the relevant content, and encoding the JSON along `templateId` lines. 
* **Automated functional tests** - [90% plus code coverage](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/documents/test-coverage.png). (See [User Instructions: Test Coverage and Reporting in CircleCI](#test-coverage-and-reporting-in-circleci) for a guide to viewing live reports.)
* **Code review process** - Using GitHub's [PR and code review](https://github.com/flexion/adele-bpa-qpp-conversion-tool/pulls?q=is%3Apr+is%3Aclosed) for merges from feature branches to main
* **Continuous Integration** - CircleCI performing [Full Continuous Integration](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/documents/continuous-integration.png).

### Where we would raise the bar in the real project

Here are the areas where we would go further in the actual project than in the design exercise.

* More thorough validation - We implemented two `templateId` validators to demonstrate how validation would be invoked, and how validation messages would be presented to users.
* Continuous user feedback - We would have our users be part of the daily process, working with the file converter to provide continuous feedback.

## Technologies

Our chosen technologies include:

* Java 8 - <https://java.com>
* Maven 3 - <https://maven.apache.org>
* CircleCI - <https://circleci.com>

## Design Process

Below are the steps we would normally take to develop a user story. Because this is a demo constructed in a compressed time frame by a partial team, some of the steps are "stubs," listed so you can see where they fall in the process. Links will take you to the artifacts that are associated with the step in the process.

Each story goes through all of these process steps. The number of user stories that go through the cycle together is a function of the maturity of the Scrum team. As we improve our processes, the size of each "batch" decreases while the value delivered each sprint increases.

### 1. Identify persona and epic or user story.

The purpose of creating personas and their associated epics and user stories is simply to better understand the design problems that need to be solved and who we are solving them for.

1. **Name and describe the primary user persona** - [Joseph](<https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/documents/QPP%20Conversion%20Tool%20Persona%20-%20Joseph.pdf>)

1.  **Tell the user story.** At this point, we recommend that the PO focus on business-level stories so that option space of system interactions (to achieve the business goal) is as large as possible. The UX design process explores this space of options. Here is the user story we are assuming for this exercise:

    > As a Perfomance Improvement IT Analyst at a large hospital, in order to submit measures to CMS, I want to convert a number of QRDS-III files produced by our existing sofware systems into the new QPP format.

### 2. Refine the Product Backlog.

The purpose of refining the backlog is to subsume new PO goals into the backlog with just enough detail to be able to prioritize it.

1. **The PO explains the persona and tells the user story.**

1. **Team asks clarifying questions.** If the team learns that the story is big, they "promote" it to an "epic."

1.  **They negotiate and converge on draft acceptance criteria.** Acceptance criteria are the business conditions that must be true when the story is successfully completed. Detailed user interactions are not included but emerge during the development process.

    > Acceptance Criteria:
    >
    > Given that there are a number of QRDA-III files in a given directory, the IT Analyst can convert them all to the QPP format using a single, simple command.

4.  **The team brainstorms options for meeting the goal.** Options can be mutually exclusive, or independent features that contribute to the overall goal. If the goal is high-level, these options are expressed as finer-grained user stories.

5.  **The team estimates the value-flow rate relative to other user stories.** Two components are inputs to value-flow rate: relative cost of delay (estimated by business) and relative duration (estimated by Scrum team).

### 3. Plan the Sprint.

The purpose of Sprint Planning is to pull enough work into the next upcoming sprint to keep the team allocated but as focused as possible in their current state. The team drafts a plan for how to organize the work in the sprint. The granularity of atomic user stories for the Product Owner will likely require that they span multiple sprints, especially when UX research, testing, and deployment are included. So sprint planning is also where atomic PO user stories might be decomposed into smaller demonstrable user stories that can be ingested into the sprint.

1.  **The PO introduces upcoming stories in value-flow rate order.**

    For the design exercise, here are the stories we accepted into the sprint:

    > As a Performance Improvement IT Analyst, in order to convert ACI Numerator Denominator Type Measures, I want to fetch Aggregate Count value.
    >
    > As a Performance Improvement IT Analyst, in order to convert an ACI Numerator Denominator Type Measure, I want to convert an ACI numerator measure.
    >
    > As a Performance Improvement IT Analyst, in order to convert an ACI Numerator Denominator Type Measure, I want to convert an ACI denominator measure.
    >
    > As a Performance Improvement IT Analyst, in order to convert the ACI section, I want to convert an ACI Numerator Denominator Type Measure.
    >
    > As a Performance Improvement IT Analyst, in order to convert the clinical document, I want to convert the ACI section.
    >

    > As a Performance Improvement IT Analyst, in order to convert the QRDA-III XML file, I want to convert the clinical document.
    >
    > As a Performance Improvement IT Analyst, in order to convert QRDA-III XML files on my file system, I want to invoke the converter from the command line.
    >
    > As a Performance Improvement IT Analyst, in order to avoid renaming files, I want the converter to auto-detect QRDA-III xml files.
    >
    > As a Performance Improvement IT Analyst, in order to ensure my submission is complete, I want the converter to ensure required ACI measures are included.
    >
    > As a Performance Improvement IT Analyst, in order to convert a lot of QRDA-III files quickly, I want the converter to convert multiple files concurrently.
    >

1. **The team reviews the story with the PO.** Acceptance criteria and story point estimates are adjusted as needed. (We used [GitHub projects as a proxy for a story card](https://github.com/flexion/adele-bpa-qpp-conversion-tool/projects?query=sort%3Acreated-asc) for the purposes of this design exercise.)

1. **The team accepts stories into the sprint**. This decision is based on measured velocity and estimated story points. If the next story is too large to fit into the sprint, the team divides the acceptance criteria into pieces and writes new stories based on those. This repeats until a story can be accepted.

1. **The team crafts a Sprint Goal.** The sprint goal is a unifying goal that binds together all user stories accepted into the sprint.

1.  **The Scrum team drafts a cross-functional task list required to get the story to done.** The list includes *all* tasks from UX research to coding to testing to deployment. These tasks are just abstract enough to avoid becoming obsolete once related details of the work start to take shape.

    In this exercise we use GitHub projects to hold our tasks. Click on the GitHub project's title to drill down into the task list. Here is one example:

    [As a Performance Improvement IT Analyst, in order to convert the ACI section, I want to convert an ACI Numerator Denominator Type Measure.](https://github.com/flexion/adele-bpa-qpp-conversion-tool/projects/5)

### 4. Execute the Sprint.

This is the bulk of the two-week sprint where the development team *develops*. This includes identifying and evaluating options, developing them, testing them, and completing all tasks in the Definition of Done (DoD). This process isn't linear but can go through multiple cycles before it's complete. Also, although these can be thought of as identical cycles, the order at which these things can happen is fluid and emerges based on need.

1.  **Create automated ATDD/BDD tests from the static click-through and the business model.** These tests drive production development and are integrated into the CI/CD pipeline.

    We have automated tests to drive development at multiple levels. Within the ["java-conversion-tool/src/test/java/gov/cms/qpp" directory](https://github.com/flexion/adele-bpa-qpp-conversion-tool/blob/master/java-conversion-tool/src/test/java/gov/cms/qpp)

1. **The development team collaborates to fully build out the user story and its supporting unit tests.** Implementation decouples the sofware components to the greatest extent possible.

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

## Test Coverage and Reporting in CircleCI

We've used [CircleCI](http://circleci.com) for our CI/CD pipeline. In order to view live reports in CircleCI there are a few extra steps you'll need to take.  

Reviewers will need to authenticate within CircleCI while logged into GitHub with the 'cmsuser1' account. The steps to follow are:

1. Log into Github with the 'cmsuser1' account
1. Navigate to [CircleCI](http://circleci.com) and select "Log In"
1. Select "Log In With GitHub"
1. Follow the "flexion/adele-bpa-qpp-conversion-tool" project

Once you've followed this project, the "live" links to specific reports below should work correctly.

### CircleCI Report Examples

* **Automated tests** - [90% plus code coverage](https://311-82203407-gh.circle-artifacts.com/0/tmp/circle-artifacts.IDxNdoM/jacoco/index.html).
* **Continuous Integration** - CircleCI performing [full continuous integration](https://circleci.com/gh/flexion/adele-bpa-qpp-conversion-tool/311) and requiring a high bar to pass.
* **SonarLint** - [Linting and source code quality metrics](https://311-82203407-gh.circle-artifacts.com/0/tmp/circle-artifacts.IDxNdoM/sonarlint/sonarlint-report.html) 

## Things we will address in the next sprint

We iterated to a desired solution, with a desire to demponstrate the key features of our proposed solution, in essentially one sprint with a skeleton crew. We moved at pace that is not sustainable. As a result, some issues and problems have emerged that we will address in the next sprint, as we move to a more deliberate pace. Here's what we would address:

1. Ensure all criteria in the DoD have been met. 
1. The wrapping class that handles JSON has a slightly complex feature that could be simplified. Instead of fetching out the inner contents of a sub-object, the wrapper class could do that on its own which would make subsequent decoders simpler.
1. Apply instrumentation to analyze performance.
1. In the code `AciProportionMeasure*` should be renamed to `AciNumDenomMeasure*`
1. Knock out the higher priority SonarLint problems.
1. Extract duplicated logic in encoders.

