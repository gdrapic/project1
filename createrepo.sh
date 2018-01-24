#!/bin/bash

#set -x

#git clone git@bitbucket.org:jcocktail/jcocktail.git
###############
# User input
###############

echo -n "Enter new Repo Name and press [ENTER]: ";
read git_repo_name_raw;
git_repo_name=${git_repo_name_raw,,};
echo "New Repo Name: $git_repo_name"

echo -n "Enter your Stash user name and press [ENTER]: ";
read stash_user_name
echo "Stash user name: $stash_user_name"

echo -n "Enter your password and press [ENTER]: ";
read -s stash_user_password
#echo "Stash user password: $stash_user_password"

################
# Constants
################
token=$(echo -n "$stash_user_name:$stash_user_password" | openssl base64);
stash_project=ifxsf
group_id=com.td.edpp.sf

git_user_name=$(git config user.name)
git_user_email=$(git config user.email)

echo "token: $token";
echo "stash_project: $stash_project";
echo "group_id: $group_id";
echo "git_repo_name: $git_repo_name";
echo "git_user_name: $git_user_name";
echo "git_user_email: $git_user_email";

read -p "Press [Enter] key to start repo creation.....";

######################
# Create local repo
######################
rm -rf $git_repo_name
mkdir $git_repo_name
cd $git_repo_name

git init

cat <<EOF > pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>$group_id</groupId>
	<artifactId>$git_repo_name</artifactId>
	<packaging>jar</packaging>
	<version>0.1-SNAPSHOT</version>
	<name>$git_repo_name</name>
	<description>EDPP Security Framework - [ENTER DESCRIPTION]</description>

	<scm>
		<connection>scm:git:ssh://git@repo.dcts.myrepo.com:7999/$stash_project/\${project.artifactId}.git</connection>
		<developerConnection>scm:git:ssh://git@repo.dcts.myrepo.com:7999/$stash_project/\${project.artifactId}.git</developerConnection>
		<url>ssh://git@repo.dcts.myrepo.com:7999/$stash_project/\${project.artifactId}.git</url>
		<tag>HEAD</tag>
	</scm>

	<distributionManagement>
		<repository>
			<id>nexus</id>
			<name>InfoEx Releases</name>
			<url>https://repo.dcts.myrepo.com:9443/content/repositories/releases/</url>
		</repository>
		<snapshotRepository>
			<id>nexus</id>
			<name>InfoEx Snapshots</name>
			<url>https://repo.dcts.myrepo.com:9443/content/repositories/snapshots/</url>
		</snapshotRepository>
	</distributionManagement>

	<dependencies>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.11</version>
			<scope>test</scope>
		</dependency>
	</dependencies>

</project>
EOF

cat pom.xml

#commit locally
git add pom.xml
git commit -m "Initial version" --author "$git_user_name <$git_user_email>"

#exit 0;

####################
# Create stash repo
####################
curl -H "Authorization: Basic $token" -H "Content-Type: application/json" \
-X POST \
"https://repo.dcts.myrepo.com/rest/api/1.0/projects/${stash_project}/repos/" -d "{\"name\": \"${git_repo_name}\"}"

###################
# Push to statsh
###################
#add to remote Stash server
git remote add origin ssh://git@repo.dcts.myrepo.com:7999/${stash_project}/${git_repo_name}.git
git push origin master

############################
# Create develoment branch
############################
git checkout -b development
git push origin development

###############################
# Grant default permission
###############################
#grant permission on repo level
curl -H "Authorization: Basic $token" -H "Content-Type: application/json" \
-X PUT \
"https://repo.dcts.myrepo.com/rest/api/1.0/projects/${stash_project}/repos/${git_repo_name}/permissions/groups?permission=REPO_WRITE&name=developers"
echo ""

###################################
# Grant permission on branch level
###################################
curl -H "Authorization: Basic $token" -H "Content-Type: application/json" \
-X POST \
"https://repo.dcts.myrepo.com/rest/branch-permissions/1.0/projects/${stash_project}/repos/${git_repo_name}/restricted" \
-d '{ "type": "BRANCH",  "value": "refs/heads/master", "users":[ ], "groups" : ["admins"]}'
echo ""

curl -H "Authorization: Basic $token" -H "Content-Type: application/json" \
-X POST \
"https://repo.dcts.myrepo.com/rest/branch-permissions/1.0/projects/${stash_project}/repos/${git_repo_name}/restricted" \
-d '{ "type": "BRANCH",  "value": "refs/heads/development", "users":[ ], "groups" : ["developers"]}'
echo ""
 
