pipeline {
   agent any
   environment {
       registry = "sibendudas/orderservice"   
       registryCredential = 'dockerhubcredentials'    
       dockerImage = ''
   }
   stages {
       stage('Build') {
           steps {
               script{
                   echo "Build stage"         
                   bat "gradle clean build" 
               }
           }
       }
       stage('Containerize') {
           steps {
               script{
                   echo "Dockerizing the build"      
                   dockerImage = docker.build registry 
               }
           }
       }
       stage('Push to Regisrty') {
           steps {
               script{
                  echo "Pushing docker image to registry"         
                  docker.withRegistry( '', registryCredential ) {
                     dockerImage.push()
                  }
               }
           }
       }
       stage('Deploy to K8S') {
           steps {
               script{
                  echo "Deploying to K8S"         
                  bat "kubectl delete -f deploy_orderservice.yaml -n ecomm1"
                  bat "kubectl apply -f deploy_orderservice.yaml -n ecomm1"
               }
           }
       }
      stage('Test') {
           steps {
               script{
                  echo "Testing deployment .."         
               }
           }
       }
   }
}
