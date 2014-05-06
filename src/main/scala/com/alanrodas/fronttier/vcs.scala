/**
 * All files in this project are distributed using
 * an Apache 2.0 licence which you may find at
 *
 * http://opensource.org/licenses/Apache-2.0
 *
 * Created by: alanrodas
 * Last modification: 06/05/14 at 14:47
 */
package com.alanrodas.fronttier

abstract class VCS(rootDir : String, remoteUrl : String) {
	/*
	HEAD: The latest revision in the repository.
	BASE: The "pristine" revision of an item in a working copy. Matches checked out version before any modifications.
	COMMITTED: The last revision in which an item changed before (or at) BASE.
	PREV: The revision just before the last revision in which an item changed. (Technically, COMMITTED - 1.)
	*/

}

object GIT {

	sealed trait GitConfigLocation
	case object GLOBAL extends GitConfigLocation
	case object SYSTEM extends GitConfigLocation

	/**
	 * Sets configuration values for your user name, email, gpg key, preferred diff algorithm,
	 * file formats and more. Example: git config --global user.name "My Name"
	 * git config --global user.email "user@domain.com" cat ~/.gitconfig [user]
	 * name = My Name email = user@domain.com
	 */
	def config(property : String, value : String, location : GitConfigLocation = GLOBAL): Unit = {}

	/**
	 * Makes a Git repository copy from a remote source. Also adds the original location
	 * as a remote so you can fetch from it again and push to it if you have permissions.
	 * Example: git clone git@github.com:user/test.git
	 */
	def clone(url : String) : GIT = {return GIT(".", url)}

	/**
	 * Initializes a git repository – creates the initial ‘.git’ directory in a new or in
	 * an existing project. Example: cd /home/user/my_new_git_folder/ git init
	 */
	def init : GIT = {return GIT(".", "")}
}

case class GIT(rootDir : String, remoteUrl: String) extends VCS(rootDir, remoteUrl) {

	/**
	 * Sets configuration values for your user name, email, gpg key, preferred diff algorithm,
	 * file formats and more. Example: git config --global user.name "My Name"
	 * git config --global user.email "user@domain.com" cat ~/.gitconfig [user]
	 * name = My Name email = user@domain.com
	 */
	def config(property : String, value : String): Unit = {}

	/**
	 * Adds files changes in your working directory to your index. Example: git add .
	 */
	def add(resource : String) : Unit = {}

	/**
	 * Removes files from your index and your working directory so they will not be
	 * tracked. Example: git rm filename
	 */
	def rm(resource : String) : Unit = {}

	/**
	 * Takes all of the changes written in the index, creates a new commit object pointing to it and sets
	 * the branch to point to that new commit. Examples: git commit -m ‘committing added changes’
	 * git commit -a -m ‘committing all changes, equals to git add and git commit’
	 */
	def commit(message : String = "") : Unit = {}

	/**
	 * git commit --amend
	 */
	def amend(message : String = "") : Unit = {}

	/**
	 * Shows you the status of files in the index versus the working directory.
	 * It will list out files that are untracked (only in your working directory),
	 * modified (tracked but not yet updated in your index), and staged (added to
	 * your index and ready for committing). Example: git status # On branch master
	 * # # Initial commit # # Untracked files: # (use "git add <file>..." to include in
	 * what will be committed) # # README nothing added to commit but untracked files
	 * present (use "git add" to track)
	 */
	def status : Unit = {}

	/**
	 * Lists existing branches, including remote branches if ‘-a’ is provided. Creates a new
	 * branch if a branch name is provided. Example: git branch -a * master remotes/origin/master
	 */
	def branch : Unit = {}

	/**
	 * Checks out a different branch – switches branches by updating the index, working tree,
	 * and HEAD to reflect the chosen branch. Example: git checkout newbranch
	 */
	def checkout : Unit = {}

	/**
	 * Merges one or more branches into your current branch and automatically creates a new commit
	 * if there are no conflicts. Example: git merge newbranchversion
	 */
	def merge : Unit = {}

	/**
	 * Resets your index and working directory to the state of your last commit. Example: git reset --hard HEAD
	 */
	def reset : Unit = {}

	/**
	 * Temporarily saves changes that you don’t want to commit immediately. You can apply the changes later.
	 * Example: git stash Saved working directory and index state "WIP on master: 84f241e first commit" HEAD is
	 * now at 84f241e first commit (To restore them type "git stash apply")
	 */
	def stash : Unit = {}

	/**
	 * Tags a specific commit with a simple, human readable handle that never moves.
	 * Example: git tag -a v1.0 -m 'this is version 1.0 tag'
	 */
	def tag : Unit = {}

	/**
	 * Fetches all the objects from the remote repository that are not
	 * present in the local one. Example: git fetch origin
	 */
	def fetch : Unit = {}

	/**
	 * Fetches the files from the remote repository and merges it with your local one.
	 * This command is equal to the git fetch and the git merge sequence. Example: git pull origin
	 */
	def pull : Unit = {}

	/**
	 * Pushes all the modified local objects to the remote repository and advances its branches. Example: git push origin master
	 */
	def push : Unit = {}

	/**
	 * Shows all the remote versions of your repository. Example: git remote origin
	 */
	def remote : Unit = {}

	/**
	 * Shows a listing of commits on a branch including the corresponding details.
	 * Example: git log commit 84f241e8a0d768fb37ff7ad40e294b61a99a0abe Author:
	 * User <user@domain.com> Date: Mon May 3 09:24:05 2010 +0300 first commit
	 */
	def log : Unit = {}

	/**
	 * Shows information about a git object. Example: git show commit 84f241e8a0d768fb37ff7ad40e294b61a99a0abe
	 * Author: User <user@domain.com> Date: Mon May 3 09:24:05 2010 +0300 first commit diff --git
	 * a/README b/README new file mode 100644 index 0000000..e69de29
	 */
	def show : Unit = {}

	/**
	 * hows a tree object, including the mode and the name of each item and the SHA-1 value of the
	 * blob or the tree that it points to. Example: git ls-tree master{tree} 100644 blob
	 * e69de29bb2d1d6434b8b29ae775ad8c2e48c5391 README
	 */
	def lsTree : Unit = {}

	/**
	 * Used to view the type of an object through the SHA-1 value.
	 * Example: git cat-file -t e69de29bb2d1d6434b8b29ae775ad8c2e48c5391 blob
	 */
	def catFile : Unit = {}

	/**
	 * Lets you search through your trees of content for words and phrases. Example: git grep "www.siteground.com" -- *.php
	 */
	def grep : Unit = {}

	/**
	 * Generates patch files or statistics of differences between paths or files in your git
	 * repository, or your index or your working directory. Example: git diff
	 */
	def diff : Unit = {}

	/**
	 * Creates a tar or zip file including the contents of a single tree from your repository.
	 * Example: git archive --format=zip master README >file.zip
	 */
	def archive : Unit = {}

	/**
	* Garbage collector for your repository. Optimizes your repository. Should be run occasionally.
	 * Example: git gc Counting objects: 7, done. Delta compression using up to 2 threads.
	 * Compressing objects: 100% (5/5), done. Writing objects: 100% (7/7), done. Total 7 (delta 1), reused 0 (delta 0)
	*/
	def gc : Unit = {}

	/**
	 * Does an integrity check of the Git file system, identifying corrupted objects. Example: git fsck
	*/
	def fsck : Unit = {}

	/**
	 * Removes objects that are no longer pointed to by any object in any reachable branch. Example: git prune
	 */
	def prune : Unit = {}
}

class SVN {
	def add : Unit = {}
	def blame : Unit = {}
	def checkout : Unit = {}
	def commit : Unit = {}
	def copy : Unit = {}
	def delete : Unit = {}
	def diff : Unit = {}
	def export : Unit = {}
	def info : Unit = {}
	def list : Unit = {}
	def lock : Unit = {}
	def log : Unit = {}
	def merge : Unit = {}
	def mkdir : Unit = {}
	def move : Unit = {}
	def revert : Unit = {}
	def resolved : Unit = {}
	def status : Unit = {}
	def switch : Unit = {}
	def update : Unit = {}
}
