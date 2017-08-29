# auto-summarizer

This document will provide details regarding the web application auto-summarizer, providing details on the application summary, details of files, terminology, as well as technical details regarding the development of the application.

## Getting Started
auto-summarizer can be cloned using both HTTPS and SSH via git commands, it requires maven to successfully import the Stanford NLP parser to handle getting parts of speech of certain sentences.

### Prerequisites

To download and be able to make edits to the auto-summarizer project you will need to install git [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

This project is a Maven project so you should Maven installed. You can follow the directions to install maven at the following:
* [Mac/OSX](https://www.mkyong.com/maven/install-maven-on-mac-osx/)
 * If [Homebrew](https://brew.sh) is installed, run `brew install maven` in your terminal
* [Windows](https://www.mkyong.com/maven/how-to-install-maven-in-windows/)
* Linux: from the terminal run `wget http://mirror.olnevhost.net/pub/apache/maven/binaries/apache-maven-3.2.1-bin.tar.gz` to install maven.

### HTTPS

Navigate in your terminal/command-prompt to the folder in which you would like to store the application.

In your terminal/command-prompt type in `git clone https://github.com/jkys/auto-summarizer.git` and press `enter` or `return` to start downloading all the files into the select folder.

### SSH

Navigate in your terminal/command-prompt to the folder in which you would like to store the application.

In your terminal/command-prompt type in `git clone git@github.com:jkys/auto-summarizer.git` and press `enter` or `return` to start downloading all the files into the select folder.

## Summary

The auto-summarizer application is a program entirely written in Java, which when given an article or block of text, it will attempt to condense that text to certain percentage of words to allow quicker reading while keeping the most amount of important details.

The processes and techniques used in the application are derived from a branch of artificial intelligence called natural language processing. The following topics were heavily used within the application [term frequency/inverse document frequency](https://en.wikipedia.org/wiki/Tf–idf) and [part of speech tagging](part-of-speech tagging).

## Built With

* [Stanford NLP Library](https://nlp.stanford.edu/software/) - For the NLP processor

## Built In

* Java

## Authors

* [Jonathan Keys](https://github.com/jkys) - Initial work/maintenance
* [Colby Daly](https://github.com/ColbyDaly) - Initial work

See also the list of [contributors](https://github.com/jkys/auto-summarizer/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/jkys/auto-summarizer/blob/master/LICENSE.md) file for details

## Acknowledgments

* A big thank you for the incredibly in depth and well documented Stanford NLP library as it was a tremendous help in learning natural language processing.
