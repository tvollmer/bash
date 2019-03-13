# bash

## osx .profile
```bash
# source git-completion
. /usr/local/git/contrib/completion/git-completion.bash
. /usr/local/git/contrib/completion/git-prompt.sh

alias lh="ll -hs"
alias hv="hg di | vim -"
alias hs="hg st"
alias v="vim"
alias gl="git log -n 15 --summary --color --graph --oneline"

alias gd="git diff -b"
alias gdmh="git diff -b master HEAD"
alias grm="git rebase master"
alias gs="git status"
alias dos2unix="perl -i -pe 's/\r\n|\n|\r/\n/g'"
alias unix2dos="perl -i -pe 's/\r\n|\n|\r/\r\n/g'"

function gh() {
  if [ "$#" == "1" ]; then
    git hist -n "$1"
  else
    git hist "$@"
  fi
}

# aliases don't accept args, but functions do
#alias cl="cd | ll -h"
function cl {
  cd $@;
  ll -h;
}

#export JAVA_HOME=/Library/Java/Home
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.6.0_22-b04-307.jdk/Contents/Home
export HOSTNAME # need to do this for mac

export PROMPT_COMMAND='echo -ne "\033]0;${USER}@${HOSTNAME%%.*}: ${PWD/#$HOME/~}\007"'

# keep an eye on this for performance issues.
# export PS1='\h:\W \u\$ '
source $(locate git-completion.bash | head -n 1)
export PS1='\u@\h: \W$(__git_ps1 " (%s)")\$ '

export PATH=~/bin:$PATH:~/Library/Python/2.7/bin

# relocated repo
export M2_REPO=~/.m2/repository

# tell grep not to look in .svn folders
#export GREP_OPTIONS="--exclude-dir=.svn"

# editor
export EDITOR=vim

# using jrebel
# export JAVA_OPTS="-javaagent:/usr/local/jrebel/current/jrebel.jar $JAVA_OPTS"

export JAVA_HOME="$(/usr/libexec/java_home)"
export EC2_PRIVATE_KEY="$(/bin/ls "$HOME"/.ec2/pk-*.pem | /usr/bin/head -1)"
export EC2_CERT="$(/bin/ls "$HOME"/.ec2/cert-*.pem | /usr/bin/head -1)"
export EC2_HOME="/usr/local/Cellar/ec2-api-tools/1.6.12.0/libexec"

export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"  # This loads nvm
```

## .gitconfig
```bash
[user]
        name = Tim Vollmer
        email = timjvollmer@gmail.com
[color]
        diff = auto
        status = auto
        branch = auto
[alias]
        st = status
        co = commit
        di = diff
        lg = log -v --graph --oneline --decorate --all
        hist = log --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr)%Creset %C(bold blue)<%an>%Creset' --abbrev-commit --date=relative --all
        up = add -u
        rd = !git status | grep deleted | cut -c 15- | xargs git rm --
        re = "!f(){ git svn rebase && git svn fetch && git diff $@;  }; f"

[core]
        excludesfile = /Users/timjvollmer/.gitignore_global
        autocrlf = input
[push]
        default = simple
```
