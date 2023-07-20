from locust import HttpUser, task, run_single_user

class HelloWorldUser(HttpUser):
     host = "http://localhost:8080"

     @task
     def hello_world(self):
         self.client.get("/")

if __name__ == "__main__":
    run_single_user(HelloWorldUser)

