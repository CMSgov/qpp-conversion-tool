# Contributing to QPP Conversion Tool

We would love for you to contribute to the QPP Conversion Tool. As a contributor, here are the guidelines we would like you to follow:

 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submit)
 - [Coding Rules](#rules)

## <a name="question"></a> Got a Question or Problem?
If you have a question or problem, please [submit an issue](#submit-issue) to our [GitHub Repository](https://github.com/CMSgov/qpp-conversion-tool).

## <a name="issue"></a> Found a Bug?
If you find a bug in the source code, you can help us by
[submitting an issue](#submit-issue) to our [GitHub Repository](https://github.com/CMSgov/qpp-conversion-tool). Even better, you can
[submit a Pull Request](#submit-pr) with a fix.

## <a name="feature"></a> Missing a Feature?
You can *request* a new feature by [submitting an issue](#submit-issue) to our GitHub
Repository. If you would like to *implement* a new feature, please submit an issue with
a proposal for your work first, to be sure that we can use it.

## <a name="submit"></a> Submission Guidelines

### <a name="submit-issue"></a> Submitting an Issue

Before you submit an issue, please search the issue tracker. It's possible an issue has already been created addressing your problem and the discussion might inform you of workarounds readily available.

We want to fix all issues as soon as possible, but before fixing a bug we need to be able to reproduce and confirm it. Please provide:

- version of qpp-conversion-tool used
- 3rd-party libraries and their versions
- most importantly, a use-case that fails

To be respectful of maintainer's time and ultimately fix more bugs, we will require a **minimal reproduce scenario**.

You can file new issues by filling out our [new issue form](https://github.com/CMSgov/qpp-conversion-tool/issues/new).

Please see [SUPPORT](https://github.com/CMSgov/qpp-conversion-tool/blob/master/.github/SUPPORT.md) for more details on the issue submission process.

### <a name="submit-pr"></a> Submitting a Pull Request (PR)

Before you submit your Pull Request (PR) consider the following guidelines:

* Search [GitHub](https://github.com/CMSgov/qpp-conversion-tool/pulls) for an open or closed PR that relates to your submission. You don't want to duplicate effort.
* Make your changes in a personal fork of the repository. See [GitHub](https://help.github.com/articles/fork-a-repo/) for help on creating a fork.
* Create your patch, **including appropriate test cases**.
* Follow our [Coding Rules](#rules).
* Run the full test suite, as described in the [developer documentation][dev-doc], and ensure that all tests pass.
* Commit your changes using a descriptive commit message.

     ```shell
     git commit -a
     ```
  Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* Push your changes on your fork to GitHub:

    ```shell
    git push
    ```

* In GitHub, send a pull request from your fork to `CMSgov/qpp-conversion-tool:master`.
* If we suggest changes then:
  * Make the required updates.
  * Re-run the full test suites to ensure tests are still passing.
  * Rebase your branch and force push to your GitHub repository (this will update your Pull Request):

    ```shell
    git rebase master -i
    git push -f
    ```

That's it! Thank you for your contribution!

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub either through the GitHub web UI or your local shell as follows:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the master branch:

    ```shell
    git checkout master -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your master with the latest upstream version:

    ```shell
    git pull --ff upstream master
    ```

## <a name="rules"></a> Coding Rules
To ensure consistency throughout the source code, please keep these rules in mind as you are working:

* All features or bug fixes **must be tested** by one or more unit-tests. All new code must have 95% or higher code coverage.
* All public API methods **must be documented**.
* Please consider installing the following [git hooks][truffle-hooks] after cloning the repo. They will help prevent unintentional introduction of secrets into history. Look [here][git-hooks] for a primer on git hooks.

[git-hooks]: https://git-scm.com/book/gr/v2/Customizing-Git-Git-Hooks
[truffle-hooks]: https://github.com/clydet/truffleHog/tree/master/truffleHooks
[github]: https://github.com/CMSgov/qpp-conversion-tool
[dev-doc]: https://github.com/CMSgov/qpp-conversion-tool/blob/master/DEVELOPER.md
