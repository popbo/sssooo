node{
    def robots = '''{
        "Stdc-Visual": "2ecf4e16-e2da-b434-12a8-94ba4edc5ef7",
    }'''
    def robot = readJSON text: robots
    try {
        run_stages()
    } catch (e) {
        throw e
    }
}


def run_stages(){
    stage('配置信息'){
        common = '''{
            "jenkins_harbor_cret": "ea9d47ea-5858-425b-8ded-37ea636449c3",
            "harbor_host": "10.255.111.87",
            "img_directory": "unittec",
            "img_author": "lixinxin",
            "pom":"./stdc-visual-service/stdc-visual-api/pom.xml",
            "sonar_url": "http://10.255.102.190:9000",
            "sonar_cert_id": "428b0d24-a8a2-40cc-ba8d-3ebe6dfb7401"

            //             "JENKINS_RANCHER_CERT": "e6d165e4-450a-41a6-bb03-c5e8853195ca",
            //             "IMG_HTTP_PORT": "8080",
            //             "NODE_HTTP_PORT": "18081",
            //             "IMG_MQTT_PORT": "1883",
            //             "NODE_MQTT_PORT": "11883",
            //             "RANCHER_LOGIN_URL": "https://10.255.111.50/v3",
            //             "RANCHER_PROJECT_ID": "c-csj2z:p-snn9h",
            //             "RANCHER_NAMESPACE": "default",
            //             "RANCHER_NODE_NAME": "iot-product01",
            //             "UPLOAD_FILE_PATH": "",
            //             "CONF_PATH": "",
            //             "TAOS_DRIVER_PATH": ""
        }'''
    }
    stage('检出scm'){
        checkout scm
    }
    
    stage('识别引用类型') {
        script {
            def refName = params.GIT_REF
            def refType = 'Unknown'
            def shortName = refName

            if (refName.startsWith('refs/tags/')) {
                refType = 'Tag'
                shortName = refName - 'refs/tags/'
            } else if (refName.startsWith('refs/heads/')) {
                refType = 'Branch'
                shortName = refName - 'refs/heads/'
                echo "当前shortName: ${shortName}"
                if (shortName.startsWith('origin')){
                    shortName = shortName - 'origin/'
                }
            } else {
                // 兼容非标准格式
                def isTag = sh(script: "git tag --points-at HEAD | grep -q .", returnStatus: true) == 0
                if (isTag) {
                    refType = 'Tag'
                    shortName = refName
                } else {
                    refType = 'Branch'
                    shortName = refName
                    echo "当前shortName: ${shortName}"
                    if (shortName.startsWith('origin')){
                        shortName = shortName - 'origin/'
                    }
                }
            }

            echo "引用类型: ${refType}, 名称: ${shortName}"
            env.REF_TYPE = refType
            env.REF_NAME = shortName
        }
        
    }

    stage('读取环境'){
        // 获取当前分支
        branch_name = getGitbranch_name()
        // 读取通用配置
        Common = readJSON text: common
        pom = readMavenPom file: Common['pom']
        app_name = "${pom.artifactId}"
        img_tag = "${pom.version}"
        if(env.TAGFLAG && "${TAGFLAG}"!=null && "${TAGFLAG}" != ""){
            if("${TAGFLAG}" == "ts"){
                img_tag = new Date().format('yyyyMMddHHmmss')
            }else{
                img_tag = "${TAGFLAG}"
            }
        }
        if("${pom.build.finalName}" != "null" && "${pom.build.finalName}" != ""){
            app_name = "${pom.build.finalName}"
        }
        img_name = "${app_name}-${branch_name}"
        docker_harbor_image = "${Common['harbor_host']}/${Common['img_directory']}/${img_name}:${img_tag}"
        service_name = "${img_name}"
        if(env.SERVICE_NAME && "${SERVICE_NAME}"!=null && "${SERVICE_NAME}" != ""){
            service_name = "${SERVICE_NAME}"
        }
    }
    stage('编译打包') {
        dir("./stdc-visual-service/") {
            def jdk11 = tool name: 'jdk11_local', type: 'jdk'
            withEnv(["JAVA_HOME=${jdk11}", "PATH=${jdk11}/bin:${env.PATH}"]) {
                sh "mvn clean package -DskipTests=true"
            }
        }
    }
    stage('代码质量检查') {
        if(env.SONAR_CHECK && "${SONAR_CHECK}"!=null && "${SONAR_CHECK}" == "false"){
            //跳过代码检查,提高构建效率。
            //发布版本时,必须进行代码质量检查！！！
        }else{
            dir("./stdc-visual-service/") {
                withCredentials([string(credentialsId: Common['sonar_cert_id'], variable: 'sonar_cert')]) {
                    sh "mvn sonar:sonar -D sonar.host.url=${Common['sonar_url']} -D sonar.login=${sonar_cert}"
                }
            }
        }
    }
    stage('构建镜像') {
        dir("./stdc-visual-service/docker") {
            withCredentials([usernamePassword(credentialsId: Common['jenkins_harbor_cret'], passwordVariable: 'password', usernameVariable: 'username')]) {
                sh """
                docker login ${Common['harbor_host']} -u ${username} -p ${password}
                cat > Dockerfile << EOF
FROM 10.255.111.87/library/openjdk:jre11-0-18

LABEL VERSION='1.0.0'
LABEL AUTHOR='lxx'

WORKDIR /home/visual/back
COPY stdc-visual-api.jar /home/visual/back
COPY ./drivers /home/visual/back/drivers
COPY ./static /home/visual/back/static
ENTRYPOINT ["sh", "-c", "java -Dfile.encoding=UTF-8 -Dspring.config.location=/home/visual/back/conf/application.yml -jar stdc-visual-api.jar"]
EOF
                docker build -t ${docker_harbor_image} .
                docker login ${Common['harbor_host']} -u ${username} -p ${password}
                docker push ${docker_harbor_image}
                """
            }
        }
    }
    stage('清理旧镜像') {
        dir("./") {
            sh """sh /mnt/data/jenkins/clear_old_images.sh ${docker_harbor_image}"""
        }
    }
    stage('安装部署') {
        dir("./stdc-visual-service/k8s") {
            withCredentials([string(credentialsId: "${JENKINS_RANCHER_CERT}", variable: 'token')]) {
                sh """
                rancher login ${RANCHER_LOGIN_URL} --token ${token} --skip-verify --context ${RANCHER_PROJECT_ID};
                cat > app-jenkins-rancher.yml << EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${service_name}
  namespace: ${RANCHER_NAMESPACE}
spec:
  replicas: 1
  selector:
    matchLabels:
      workload.user.cattle.io/workloadselector: deployment-${RANCHER_NAMESPACE}-${service_name}
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
    type: RollingUpdate
  template:
    metadata:
      labels:
        workload.user.cattle.io/workloadselector: deployment-${RANCHER_NAMESPACE}-${service_name}
    spec:
      imagePullSecrets:
        - name: harbor-cert
      affinity:
        nodeAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - preference:
                matchExpressions:
                  - key: kubernetes.io/hostname
                    operator: In
                    values:
                      - ${RANCHER_NODE_NAME}
              weight: 1
      nodeName: ${RANCHER_NODE_NAME}
      restartPolicy: Always
      containers:
        - name: ${service_name}
          image: ${Common['harbor_host']}/${Common['img_directory']}/${img_name}:${img_tag}
          imagePullPolicy: Always
          ports:
            - name: ${IMG_HTTP_PORT}tcp${NODE_HTTP_PORT}
              containerPort: ${IMG_HTTP_PORT}
          securityContext:
            privileged: true
          volumeMounts:
            - mountPath: /home/visual/back/conf/application.yml
              name: application-conf
              subPath: application.yml
      volumes:
      - configMap:
          defaultMode: 256
          name: stdc-visual-back-cm
          optional: false
        name: application-conf

---
apiVersion: v1
kind: Service
metadata:
  name: ${service_name}
  namespace: ${RANCHER_NAMESPACE}
spec:
  ports:
    - name: http
      port: ${IMG_HTTP_PORT}
      targetPort: ${IMG_HTTP_PORT}
      nodePort: ${NODE_HTTP_PORT}
  selector:
    workload.user.cattle.io/workloadselector: deployment-${RANCHER_NAMESPACE}-${service_name}
  type: NodePort
EOF
                cat app-jenkins-rancher.yml
                rancher kubectl apply -f app-jenkins-rancher.yml
                rancher kubectl rollout restart deployment/${service_name} --namespace ${RANCHER_NAMESPACE}
                """
            }
        }
    }
}

// 获取分支名称
def getGitbranch_name() {
    return scm.branches[0].name
}