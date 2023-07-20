# 필요한 기능 import 필요
from locust import HttpUser, task, run_single_user, constant_throughput
import random
import logging

# 스크립트 요구사항
#       - 조회 API : GET /api/owner/{id}
#          - 캐시 효과를 없애기 위해, 미리 owner 리스트를 확보하고, 이중에서 랜덤으로 특정 owner 조회 호출
#       - 생성 API : POST /api/owner
#       - Stress 테스트를 위해 분당 100개의 task 만을 실행
#       - 조회 100회당 생성 1회 실행
#
# 실행 요구사항
#      - headless 모드로 100개 유저를 사용하여 1분간 실행
#      - master 1개와 worker 2개로 실행
class OwnerUser(HttpUser):
    # 디폴트 호스트. debug 모드시에 유용
    host = "http://localhost:8080"
    # 1초에 지정한 숫자 만큼의 task 만이 실행되도록 동적으로 wait_time 부여 가능.
    # wait_time = constant_throughput(10)
    # wait_time = between(1, 5) # 특정 초 만큼 대기도 가능

    def __init__(self, environment):
        super().__init__(environment)
        pass
        # self.owner_size = None
        # self.owner_ids = None

    def on_start(self): # 각 user 가 init 될때 실행
        pass
        # 디폴트 헤더와 auth. FastHttpUser 에서는 사용 불가
        # self.client.auth = ("admin", "admin")
        # self.client.headers = { 'content-type': 'application/json' }
        # 테스트에 필요한 데이터 사전에 패치해 둘 수 있음
        # with self.client.get("/api/owners", name="init") as response:
        #     self.owner_ids = [each["id"] for each in response.json()]
        #     self.owner_size = len(self.owner_ids)

    @task(100) # @task 를 사용하여, 반복 테스크로 지정. 가중치를 parameter 로 부여
    def owners(self):
        pass
        # 랜덤 데이터 pick
        # owner_id = self.owner_ids[random.randrange(0, self.owner_size)]
        # with self.client.get(  # get 호출. with 구문을 사용하여 응답을 처리할 수 있음
        #         f"/api/owners/{owner_id}", # interpolation
        #         name="/api/owners/[id]",   # 리포팅 그룹화
        #         catch_response=True   # 200이 아닐 때도 정상 리턴 되도록 처리
        # ) as response:
        #     if response.status_code != 200: response.failure("Got wrong response")

    @task(1)
    def owner_post(self):
        pass
        # with self.client.post("/api/owners",
        #         json={"firstName": "hello", "lastName": "world",
        #               "address": "test b3575ed804ff",
        #               "city": "test 48c4175509fd", "telephone": "1234"},
        #         name="post /api/owners/", catch_response=True
        # ) as response:
        #     if response.status_code != 201:
        #         logging.error(f"returned status code ${response.status_code}")
        #         response.failure("Got wrong response")
        #     # json 은 dict 로 검증
        #     elif response.json()["id"] is not None : # json 리턴값을 dict 로 참조
        #         response.failure("Got wrong response")

# 디버그를 할때 사용할 수 있도록 실행 방법 지정
if __name__ == "__main__":
    run_single_user(OwnerUser)

