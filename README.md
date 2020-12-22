# Most-Freq-Words-In-URL
This is a Maven based project. This project finds the most frequent words in a given URL & nested URL with a defined limit

**Steps to build & execute the project on terminal :**
1. Clone the project locally :
git clone https://github.com/santosh-m/Most-Freq-Words-In-URL

2. Navigate inside folder FreqWord :
cd FreqWord

3. Build with below mvn command :
mvn clean install -Dmaven.test.skip=true

4. Run Main class :
mvn exec:java -Dexec.mainClass="com.freqword.FrequentWordInUrl"
//Input URL when prompted on console
//In a few secs/mins Most frequent words will be displayed on console

5. Run all test cases :
mvn test

**Steps to build & execute the project on STS IDE :**
1. Clone the project locally :
git clone https://github.com/santosh-m/Most-Freq-Words-In-URL

2. Import the cloned folder as maven project :

3. Right click on proj & select Maven -> Update project
Alternatively you can login to terminal into the project root folder & run below mvn command :
mvn clean install -Dmaven.test.skip=true

4. Run Main class :
Right click on FrequentWordInUrl.java -> Run as Java Application
//Input URL when prompted on console
//In a few secs/mins Most frequent words will be displayed on console

5. Run all test cases :
Right click on FrequentWordInUrlTest.java -> Run as Junit Test
