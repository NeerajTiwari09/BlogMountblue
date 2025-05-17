echo "Copy jar to app dir"
cp target/Blog-0.0.1-SNAPSHOT.jar app/target/
docker-compose up --build