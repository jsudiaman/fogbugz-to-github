#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "sudiamanj/fogbugz-to-github" ] && [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  javadoc -d $HOME/javadoc-latest -sourcepath src/main/java -subpackages com -noqualifier all -link https://docs.oracle.com/javase/8/docs/api/

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/sudiamanj/fogbugz-to-github gh-pages > /dev/null

  cd gh-pages
  git rm -rf *
  cp -Rf $HOME/javadoc-latest .
  git add -f .
  git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published Javadoc to gh-pages.\n"
  
fi
