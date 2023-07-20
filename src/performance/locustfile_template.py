from locust import FastHttpUser, HttpUser, task, run_single_user, events, between, constant, constant_throughput
from locust.runners import MasterRunner
import random
import logging

# locust 가 초기 init 될때 실행
# master 와 slave 에 따라 다른 처리가 필요할 때 사용
@events.init.add_listener
def on_locust_init(environment, **kwargs):
    if (isinstance(environment.runner, MasterRunner)):
        # 로그는 logging.info() 로 출력 가능
        logging.info("running on master")
    else:
        logging.info("running on slave")
    # and data load if available

# User class 는 HttpUser 클래스 상속. 더 빠른 FastHttpUser 클래스도 있으나, 일부 기능 미지원
class OwnerUser(HttpUser):
    # 디폴트 호스트. debug 모드시에 유용
    host = "http://localhost:8080"
    # 1초에 지정한 숫자 만큼의 task 만이 실행 되도록, 동적으로 wait_time 부여
    # wait_time = constant_throughput(10)
    # wait_time = constant(1) # task 1회 실행 시간이 지정한 초 만큼만 되도록 동적 wait
    # wait_time = between(1, 5) # 각 task 실행간 주어진 시간 사이의 동적 wait
    def __init__(self, environment):
        super().__init__(environment)

    # 각 user 가 init 될때 실행
    def on_start(self):
        # 디폴트 헤더와 auth. FastHttpUser 에서는 사용 불가
        # self.client.auth = ("admin", "admin")
        # self.client.headers = { 'content-type': 'application/json' }
        pass

    # @task decorator 를 사용하여 반복 수행 되는 메소드 지정
    @task
    def owners(self):
        # 랜덤으로 사용할 데이터 pick
        with self.client.get(
                # interpolation 을 위해 f 사용
                f"/api/owners/{owner_id}",
                # 동적으로 변하는 url 을 하나의 url 로 리포팅 그룹화
                name="/api/owners/[id]",
                # 직접 auth 를 넣어줄 수도 있음.
                auth=("admin", "admin"),
                # 200이 아닐 때도 정상 리턴 되도록 처리
                catch_response=True
        ) as response:
            # 리턴값 검증
            if response.status_code != 200:
                logging.error(f"returned status code is not 200 but ${response.status_code}")
                response.failure("Got wrong response")

# 디버그를 할때 사용할 수 있도록 실행 방법 지정
if __name__ == "__main__":
    run_single_user(OwnerUser)
