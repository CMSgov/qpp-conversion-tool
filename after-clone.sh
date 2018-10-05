#!/bin/sh
brew install git-secrets
git secrets --install

git secrets --register-aws


# Code from https://confluence.cms.gov/display/QPPFC/QPPGUID-228%3A+GIT+Pre-Commit+Hooks+for+Sensitive+Data
# to prevent real TINs
git secrets --add '(\D|^)\d{3} \d{2} \d{4}(\D|$)'
git secrets --add '(\D|^)\d{3}\d{2}\d{4}(\D|$)'

# to allow fake TINs
git secrets --add --allowed '(\D|^)000-\d{2}-\d{4}(\D|$)'
git secrets --add --allowed '(\D|^)000 \d{2} \d{4}(\D|$)'
git secrets --add --allowed '(\D|^)000\d{2}\d{4}(\D|$)'
git secrets --add --allowed '(\D|^)999-\d{2}-\d{4}(\D|$)'
git secrets --add --allowed '(\D|^)999 \d{2} \d{4}(\D|$)'
git secrets --add --allowed '(\D|^)999\d{2}\d{4}(\D|$)'

echo 'Installed git-secrets, and configured QPPCT rules'