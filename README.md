# 주택 금융 공급 현황 분석 서비스

## 개발 환경
- Java 8
- Spring Boot 2
- Spring Data JPA
- Lombok
- Gradle 5

## 빌드 및 실행 방법

Windows의 경우
```bash
gradlew build # 테스트 & 빌드
gradlew bootRun # 실행 (localhost:8081)
```

Mac 또는 Linux의 경우
```bash
./gradlew build # 테스트 & 빌드
./gradlew bootRun # 실행 (localhost:8081)
```

IntelliJ IDEA 환경에서 실행시키기 위해서는 Lombok 플러그인 설치 및 설정이 필요함

1. Lombok Plugin 설치방법
 * Preferences > Plugins
 * Marketplace에서 "Lombok Plugin" 검색후 설치
 * IntelliJ 재시작

2. annotation processing 설정방법
 * Preferences > Build, Execution, Deployment > Compiler > Annotation Processors
 * Enable annotation processing 을 체크

## API Reference

일부 API 는 요청시 Authorization 헤더에 JWT 토큰을 넣어주어야 한다.

---
### 계정 생성 API
**URL** : `POST /api/jwt/signup`

**Description** : 신규로 ID와 패스워드를 등록하고 토큰을 발급하는 API

**Auth Required** : NO

**Request Body Data Constraints**

| Name | Type | Mandatory | Description |
|:----:|:----:|:---------:|:-----------:|
| id | String | true | 계정의 아이디 |
| pw | String | true | 계정의 패스워드 |

**Request Example**
```bash
$ curl -X POST http://localhost:8081/api/jwt/signup \
  -H "Content-Type: application/json" \
  -d '{
    "id": "aaa",
    "pw": "bbb"
  }'
```

**Response Body Example**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMjg3N30.qcGxlUcy7GTl6PCQ1-qepUGdAvMbd3yBHP_D2c5inTU"
}
```

---
### 계정 로그인 API
**URL** : `POST /api/jwt/signin`

**Description** : 이미 등록된 ID와 패스워드로 새로운 토큰을 재발급하는 API

**Auth Required** : NO

**Request Body Data Constraints**

| Name | Type | Mandatory | Description |
|:----:|:----:|:---------:|:-----------:|
| id | String | true | 계정의 아이디 |
| pw | String | true | 계정의 패스워드 |

**Request Example**
```bash
$ curl -X POST http://localhost:8081/api/jwt/signin \
  -H "Content-Type: application/json" \
  -d '{
    "id": "aaa",
    "pw": "bbb"
  }'
```

**Response Body Example**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas"
}
```

---
### 토큰 재발급 API
**URL** : `POST /api/jwt/refresh`

**Description** : 기존에 발급받은 토큰으로 새로운 토큰을 재발급하는 API

**Auth Required** : YES

**Request Body Data Constraints** : N/A

**Request Example**
```bash
$ curl -X POST http://localhost:8081/api/jwt/refresh \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas"
```

**Response Body Example**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzQwOH0.-Ao4-xz-aaAB30RTzdGH4n_uQyJdepfm5ObpZESF9aU"
}
```

---
### csv 파일 업로드 API
**URL** : `POST /file/csv/upload`

**Description** : csv 파일을 업로드하는 API. 이미 데이터가 업로드 되어 있는 상태에서 다시 csv 파일을 업로드하면 기존 데이터는 DB에서 삭제되므로 주의.

**Auth Required** : YES

**Request Body Data Constraints**

| Name | Type | Mandatory | Description |
|:----:|:----:|:---------:|:-----------:|
| file | multipart/form-data | true | csv파일 |

**Request Example**
```bash
$ curl -X POST http://localhost:8081/file/csv/upload \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/Users/dongkwon/Downloads/fulldata.csv"
```

**Response Body Example**
```json
{
  "result": "success"
}
```

---
### 주택 금융 공급 금융기관(은행) 목록을 출력하는 API
**URL** : `GET /api/data/institutes`

**Auth Required** : YES

**Request Parameter** : N/A

**Request Example**
```bash
$ curl -X GET http://localhost:8081/api/data/institutes \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas"
```

**Response Body Example**
```json
["주택도시기금1)","국민은행","우리은행","신한은행","한국시티은행","하나은행","농협은행/수협은행","외환은행","기타은행"]
```

---
### 연도별 각 금융기관의 지원금액 합계를 출력하는 API
**URL** : `GET /api/data/amounts-by-year`

**Auth Required** : YES

**Request Parameter** : N/A

**Request Example**
```bash
$ curl -X GET http://localhost:8081/api/data/amounts-by-year \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas"
```

**Response Body Example**
```
{
   "name":"주택금융 공급현황",
   "amounts_by_year":[
      {
         "year":"2005년",
         "totalAmount":"48016",
         "detail_amount":{
            "농협은행/수협은행":"1486",
            "하나은행":"3122",
            "우리은행":"2303",
            "국민은행":"13231",
            "주택도시기금1)":"22247",
            "신한은행":"1815",
            "외환은행":"1732",
            "기타은행":"1376",
            "한국시티은행":"704"
         }
      },
      ...
      {
         "year":"2017년",
         "totalAmount":"295126",
         "detail_amount":{
            "농협은행/수협은행":"26969",
            "하나은행":"35629",
            "우리은행":"38846",
            "국민은행":"31480",
            "주택도시기금1)":"85409",
            "신한은행":"40729",
            "외환은행":"0",
            "기타은행":"36057",
            "한국시티은행":"7"
         }
      }
   ]
}
```

---
### 각 연도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관명을 출력하는 API
**URL** : `GET /api/data/top-bank`

**Auth Required** : YES

**Request Parameter** : N/A

**Request Example**
```bash
$ curl -X GET http://localhost:8081/api/data/top-bank \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas"
```

**Response Body Example**
```json
{
  "year":"2014",
  "bank":"주택도시기금1)"
}
```

---
### 전체 년도에서 외환은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액을 출력하는 API
**URL** : `GET /api/data/institutes/oehwan/min-max-amount`

**Auth Required** : YES

**Request Parameter** : N/A

**Request Example**
```bash
$ curl -X GET http://localhost:8081/api/data/institutes/oehwan/min-max-amount \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas"
```

**Response Body Example**
```json
{
  "bank":"외환은행",
  "support_amount":[
    {"year":"2017","amount":"0"},
    {"year":"2015","amount":"1701"}
  ]
}
```

---
### (선택문제) 특정 은행의 특정 달에 대해서 2018년도 해당 달에 금융지원 금액을 예측하는 API
**URL** : `POST /api/data/predict2018`

**Auth Required** : YES

**Request Body Data Constraints**

| Name | Type | Mandatory | Description |
|:----:|:----:|:---------:|:-----------:|
| bank | String | true | 기관(은행)의 이름 |
| month | String | true | 예측하려고 하는 월 |

**Request Example**
```bash
$ curl -X POST http://localhost:8081/api/data/predict2018 \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6ImFhYSIsImV4cCI6MTU1MzcyMzAyMn0.xEKSdkrOXq35o_Rf3Xbjg4j9k42d8h7hn1WzHRy8fas" \
  -H "Content-Type: application/json" \
  -d '{
    "bank": "국민은행",
    "month": "12"
  }'
```

**Response Body Example**
```json
{
  "bank":"2",
  "year":"2018",
  "month":"12",
  "amount":"3668"
}
```
---

## 문제해결 전략

#### 기본 문제 해결 전략
- 대부분의 API가 연도별 지원금액의 합계를 요구함
- 그러므로 연도별 지원금액을 미리 합산해두어 저장해두는 것이 부하경감 면에서 유리하다고 판단
- csv 파일이 업로드 될 때 연도별 금액의 sum와 average를 계산하는 배치 로직을 실행시켜 support_summary 테이블에 저장해두도록 함

#### 선택 문제 해결 전략
- 샘플 데이터 분석
    - 기관별로 금액의 규모가 크게 다르므로 특정 기관의 금액을 예측할 때 다른 기관의 데이터를 참조하는 것은 의미가 없어보임
    - 외환은행과 같은 특수 케이스가 존재
    - 하나의 기관만을 놓고 보았을 때 월별 금액의 변동 추이는 매년 비슷한 것으로 추정됨 -> 그래프 / Correlation 계산 등으로 확인 필요
    - 하나의 기관만을 놓고 보아도 연도의 차이가 많이 나면 금액의 차이가 커짐 
- 예측 모델 고안
    - 제한된 과제 기간동안 ML이나 고급 데이터 분석 기법을 공부해서 적용시키기에는 무리가 있음
    - 위에서 분석된 내용을 이용하면 2017년, 2016년 데이터 만으로도 2018년 데이터를 추정할 수 있을 것으로 보임
    - 2018년 특정월의 금액은 2017년, 2016년의 해당 월(혹은 그 주변의 데이터)의 금액과 비슷할 것으로 보고 예측 모델을 고안
