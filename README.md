# SPRING PLUS

## AWS 활용 첨부
- EC2 배포 후 Health Check
<img width="1814" height="881" alt="image" src="https://github.com/user-attachments/assets/7f8915b9-9dff-420d-be07-f5fd65f57520" />


- EC2 에 RDS 연결
<img width="1125" height="246" alt="image" src="https://github.com/user-attachments/assets/820498fd-898b-451f-a2b7-5de5d057cef0" />


- S3 저장소를 통한 프로필 이미지 저장
<img width="615" height="669" alt="image" src="https://github.com/user-attachments/assets/62b1d7b9-d1ad-4e20-a978-8972db7eb24c" />


## 500만건 검색 조회 개선
- 유저 500만건 생성 (닉네임 : nick-1... 5000000)
- 닉네임 조회

<img width="1172" height="427" alt="image" src="https://github.com/user-attachments/assets/afe12728-714a-4478-841e-7dfc087b0923" />

- 초기 소요 시간 : 1.34s

- 닉네임 인덱스 추가
```sql
create index idx_users_nickname on users (nickname);
```

<img width="1178" height="477" alt="image" src="https://github.com/user-attachments/assets/39246a6c-57ca-4ad5-a385-7855f7594b50" />

- 인덱스 추가 후 소요시간 : 42ms
