
--기존 데이터 삭제
DELETE FROM recommend_pet;
DELETE FROM dog_traits;
DELETE FROM abandoned_pet;
DELETE FROM shelter;
DELETE FROM question_traits;

-- recommend_pet 테이블 생성
CREATE TABLE IF NOT EXISTS recommend_pet (
     recommend_pet_id BIGINT PRIMARY KEY,
    species VARCHAR(50) NOT NULL,
    breed VARCHAR(100) NOT NULL,
    image_url VARCHAR(255),
    temperament VARCHAR(255),
    lifespan VARCHAR(50),
    precaution VARCHAR(255),
    breed_kor VARCHAR(100)
    );

-- dog_traits 테이블 생성
CREATE TABLE IF NOT EXISTS dog_traits (
    breed_id BIGINT PRIMARY KEY,
    breed VARCHAR(100) NOT NULL,
    expression_of_affection INT,
    affectionate_with_family INT,
    child_friendly INT,
    dog_friendly INT,
    stranger_friendly INT,
    shedding INT,
    grooming INT,
    drooling_level INT,
    health_issues INT,
    trainability INT,
    playfulness INT,
    energy_level INT,
    exercise_needs INT,
    apartment_friendly INT,
    cat_friendly INT,
    barking INT,
    social_needs INT,
    watchdog_ability INT
    );

-- abandoned_pet 테이블 생성
CREATE TABLE IF NOT EXISTS abandoned_pet (
    id BIGINT PRIMARY KEY,
    age VARCHAR(50),
    breed VARCHAR(100),
    characteristics VARCHAR(255),
    color VARCHAR(50),
    found_location VARCHAR(255),
    image_url VARCHAR(255),
    name VARCHAR(100),
    neutered_status CHAR(1),
    notice_number VARCHAR(100),
    provider_shelter_id BIGINT,
    sex CHAR(1),
    species VARCHAR(50),
    weight VARCHAR(50)
    );

-- shelter 테이블 생성
CREATE TABLE IF NOT EXISTS shelter (
   id BIGINT PRIMARY KEY,
   center_phone_number VARCHAR(50),
    city_code INT,
    district_code INT,
    eupmyeondong VARCHAR(100),
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    manager_phone_number VARCHAR(50),
    name VARCHAR(100),
    operation_end_time TIME,
    operation_start_time TIME,
    provider_shelter_id BIGINT,
    road_address VARCHAR(255),
    sido VARCHAR(50),
    sigungu VARCHAR(50),
    user_id BIGINT
    );

-- question_traits 테이블 생성
CREATE TABLE IF NOT EXISTS question_traits (
    id BIGINT PRIMARY KEY,
    question_id INT NOT NULL,
    option_id INT NOT NULL,
    trait_name VARCHAR(100) NOT NULL,
    score INT NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    tolerance INT NOT NULL,
    reverse INT NOT NULL
    );




-- recommend_pet 테이블 데이터 삽입
INSERT INTO recommend_pet (recommend_pet_id, species, breed, image_url, temperament, lifespan, precaution, breed_kor)
VALUES
    (1, 'DOG', 'Border Collie', 'https://www.akc.org/wp-content/uploads/2017/11/Golden-Retriever-wading-into-a-lake.jpg', '지능이 높고 매우 활동적임', '12-15년', '정신적 자극과 충분한 운동이 필수적임', '보더 콜리'),
    (2, 'DOG', 'Labrador Retriever', 'https://www.akc.org/wp-content/uploads/2017/11/Golden-Retriever-wading-into-a-lake.jpg', '친근하고 활발하며 차분함', '10-12년', '비만에 취약하므로 운동과 식이 관리 필요', '래브라도 리트리버'),
    (3, 'DOG', 'Poodle', 'https://www.akc.org/wp-content/uploads/2017/11/Golden-Retriever-wading-into-a-lake.jpg', '똑똑하고 활발하며 경계심이 강함', '12-15년', '정기적인 미용이 필요', '푸들'),
    (4, 'DOG', 'Shiba Inu', 'https://www.akc.org/wp-content/uploads/2017/11/Golden-Retriever-wading-into-a-lake.jpg', '독립적이며 충성스럽지만 고집이 있음', '12-15년', '어릴 때부터 충분한 사회화 훈련 필요', '시바 이누'),
    (5, 'DOG', 'Pomeranian', 'https://www.akc.org/wp-content/uploads/2017/11/Golden-Retriever-wading-into-a-lake.jpg', '활발하고 대담하며 장난기 많음', '12-16년', '짖는 습관을 훈련으로 조절해야 함', '포메라니안');

-- dog_traits 테이블 데이터 삽입
INSERT INTO dog_traits (breed_id, breed, expression_of_affection, affectionate_with_family, child_friendly, dog_friendly, stranger_friendly, shedding, grooming, drooling_level, health_issues, trainability, playfulness, energy_level, exercise_needs, apartment_friendly, cat_friendly, barking, social_needs, watchdog_ability)
VALUES
    (1, 'Border Collie', 5, 5, 5, 3, 3, 3, 2, 1, 3, 5, 5, 5, 5, 2, 3, 5, 5, 5),
    (2, 'Labrador Retriever', 5, 5, 4, 5, 3, 5, 5, 2, 5, 5, 5, 3, 5, 3, 3, 4, 5, 5),
    (3, 'Poodle', 5, 5, 5, 4, 4, 1, 5, 1, 3, 5, 5, 4, 4, 5, 4, 3, 5, 4),
    (4, 'Shiba Inu', 4, 5, 3, 3, 2, 3, 1, 1, 3, 2, 3, 3, 3, 5, 2, 3, 3, 5),
    (5, 'Pomeranian', 5, 5, 1, 3, 3, 2, 3, 1, 3, 3, 3, 3, 2, 5, 5, 4, 5, 4);


-- abandoned_pet 테이블 데이터 삽입
INSERT INTO abandoned_pet (id, age, breed, characteristics, color, found_location, image_url, name, neutered_status, notice_number, provider_shelter_id, sex, species, weight)
VALUES
    (427343202500038, '2017(년생)', '푸들', '털이 많이 엉킴', '갈색', '내당홈플러스 뒤편', 'http://www.animal.go.kr/files/shelter/2025/02/202502081702675_s.jpg', '푸들 2017(년생) (남아)', 'Y', '대구-서구-2025-00038', 327343201500001, 'M', 'DOG', '7.2(Kg)'),
    (427345202500052, '2021(년생)', '푸들', '미용, 분홍옷, 주황색 목줄 착용', '갈색', '관음동 1247-4', 'http://www.animal.go.kr/files/shelter/2025/02/202502070902378_s.jpg', '푸들 2021(년생) (여아)', 'U', '대구-북구-2025-00009', 327345201400001, 'F', 'DOG', '4(Kg)'),
    (427345202500069, '2020(년생)', '푸들', '베이지색 목줄, 주황색 패딩 착용', '회색', '복현로 22-8', 'http://www.animal.go.kr/files/shelter/2025/02/202502130902961_s.jpg', '푸들 2020(년생) (남아)', 'N', '대구-북구-2025-00011', 327345201400001, 'M', 'DOG', '7(Kg)'),
    (428356202500088, '2023(년생)', '푸들', '두마리 함께입소. 길에서발견. 순함.옷착용. 활발함. 애교많음.', '주황&흰색', '청라동 133-28', 'http://www.animal.go.kr/files/shelter/2025/01/202502031602575_s.jpg', '푸들 2023(년생) (남아)', 'N', '인천-서구-2025-00082', 328356201000001, 'M', 'DOG', '4.8(Kg)'),
    (428356202500121, '2023(년생)', '푸들', '호흡곤란, 횡격막 파열, 배에 피멍, 예후불량, 유치 흔들리고 썩음, 순하고 착함, 예쁨', '갈색', '서구청당직실', 'http://www.animal.go.kr/files/shelter/2025/02/202502161502964_s.jpg', '푸들 2023(년생) (여아)', 'U', '인천-서구-2025-00115', 328356201000001, 'F', 'DOG', '1.6(Kg)'),
    (428356202500123, '2022(년생)', '푸들', '목줄없음,온순함', '검정색', '검단소방서', 'http://www.animal.go.kr/files/shelter/2025/02/202502171002571_s.jpg', '푸들 2022(년생) (남아)', 'Y', '인천-서구-2025-00117', 328356201600005, 'M', 'DOG', '8(Kg)'),
    (428357202500061, '2017(년생)', '푸들', '좌측후지파행 / 치석매우심함', '갈색', '하점면 전망대로 1636', 'http://www.animal.go.kr/files/shelter/2025/02/202502111602405_s.jpg', '푸들 2017(년생) (여아)', 'N', '인천-강화-2025-00097', 328357202400001, 'F', 'DOG', '4.3(Kg)'),
    (430365202500022, '2024(년생)', '푸들', '특이사항 없음.', '기타(황색)', '선화서로 98 선화빌리지 주변', 'http://www.animal.go.kr/files/shelter/2025/02/202502171102783_s.jpg', '푸들 2024(년생) (여아)', 'U', '대전-중구-2025-00025', 330365202000001, 'F', 'DOG', '3(Kg)'),
    (430366202500022, '2015(년생)', '푸들', '옷, 귀저귀 착용은 신고자가함, 미용함', '흰색', '도마동 99-25 빌라 앞', 'http://www.animal.go.kr/files/shelter/2025/02/202502111802329_s.jpg', '푸들 2015(년생) (남아)', 'U', '대전-서구-2025-00021', 330366202000001, 'M', 'DOG', '2(Kg)'),
    (430368202500017, '2023(년생)', '푸들', NULL, '갈색', '신탄진로350번길', 'http://www.animal.go.kr/files/shelter/2025/01/202502031002463_s.jpg', '푸들 2023(년생) (남아)', 'U', '대전-대덕-2025-00017', 330368202000001, 'M', 'DOG', '4(Kg)');


-- shelter 테이블 데이터 삽입
INSERT INTO shelter (id, center_phone_number, city_code, district_code, eupmyeondong, latitude, longitude, manager_phone_number, name, operation_end_time, operation_start_time, provider_shelter_id, road_address, sido, sigungu, user_id)
VALUES
    (159, '053-964-6258', 6270000, 3450000, '금강로', 35.8638745, 128.7359591, '053-665-3195', '대구유기동물보호협회', '18:00:00', '09:00:00', 327345201400001, '151-13 (금강동)', '대구광역시', '동구', NULL),
    (161, '053-556-8575', 6270000, 3430000, '서대구로', 0.0000000, 0.0000000, '053-663-2646', '대구시수의사회(삼성)', '18:00:00', '09:00:00', 327343201500001, '24 (내당동)', '대구광역시', '서구', NULL),
    (180, '010-2679-3786', 6280000, 3570000, '강화읍', 37.7411832, 126.5012143, '032-930-4533', '디디동물병원', '18:00:00', '09:00:00', 328357202400001, '강화대로 254-1', '인천광역시', '강화군', NULL),
    (192, '032-575-0833', 6280000, 3560000, '가정로', 37.5156655, 126.6730219, '032-560-1902', '가정동물병원', '18:00:00', '09:00:00', 328356201000001, '346 (가정동)', '인천광역시', '서구', NULL),
    (193, '032-566-0075', 6280000, 3560000, '서곶로', 37.5488884, 126.6766991, '032-560-1902', '우리동물병원', '18:00:00', '09:00:00', 328356201600005, '349 (연희동) 우리동물병원', '인천광역시', '서구', NULL),
    (242, '042-270-7239', 6300000, 3680000, '금남구즉로', 36.4587719, 127.3828302, '042-608-6953', '대전동물보호센터', '18:00:00', '09:00:00', 330368202000001, '1234 (금고동) 대전광역시 동물보호센터', '대전광역시', '유성구', NULL),
    (246, '042-270-7239', 6300000, 3660000, '금남구즉로', 36.4587719, 127.3828302, '042-288-2483', '대전동물보호센터', '18:00:00', '09:00:00', 330366202000001, '1234 (금고동) 대전광역시 동물보호센터', '대전광역시', '유성구', NULL),
    (249, '042-270-7239', 6300000, 3650000, '금남구즉로', 36.4587719, 127.3828302, '042-606-7413', '대전동물보호센터', '18:00:00', '09:00:00', 330365202000001, '1234 (금고동) 대전광역시 동물보호센터', '대전광역시', '유성구', NULL);

-- question_traits 테이블 데이터 삽입
INSERT INTO question_traits (id, question_id, option_id, trait_name, score, weight, tolerance, reverse)
VALUES
    (1, 1, 1, 'energyLevel', 5, 1, 0, 0),
    (2, 1, 1, 'exerciseNeeds', 5, 1, 0, 0),
    (11, 2, 1, 'socialNeeds', 5, 1, 0, 0),
    (16, 3, 1, 'shedding', 5, 1, 1, 0),
    (17, 3, 1, 'grooming', 5, 1, 1, 0),
    (18, 3, 1, 'droolingLevel', 5, 1, 1, 0),
    (31, 4, 1, 'watchdogAbility', 1, 1, 0, 0),
    (36, 5, 1, 'healthIssues', 1, 1, 1, 0),
    (41, 6, 1, 'playfulness', 5, 1, 0, 0),
    (46, 7, 1, 'strangerFriendly', 5, 1, 0, 0),
    (47, 7, 1, 'socialNeeds', 5, 0.8, 0, 0),
    (52, 8, 1, 'barking', 1, 1, 0, 0),
    (57, 9, 1, 'childFriendly', 5, 1, 0, 0),
    (60, 10, 1, 'apartmentFriendly', 5, 1, 0, 0),
    (61, 10, 1, 'energyLevel', 1, 0.5, 0, 0),
    (62, 10, 1, 'exerciseNeeds', 1, 0.5, 0, 0),
    (71, 11, 1, 'energyLevel', 1, 0.8, 0, 0),
    (72, 11, 1, 'exerciseNeeds', 1, 1, 0, 0),
    (81, 12, 1, 'watchdogAbility', 5, 1, 0, 0),
    (85, 13, 1, 'affectionateWithFamily', 1, 1, 0, 0),
    (86, 13, 1, 'expressionOfAffection', 1, 1, 0, 0),
    (95, 14, 1, 'grooming', 5, 1, 0, 0),
    (100, 15, 1, 'energyLevel', 5, 1, 0, 0),
    (101, 15, 1, 'exerciseNeeds', 5, 1, 0, 0),
    (108, 16, 1, 'barking', 1, 0.2, 0, 1),
    (114, 17, 1, 'droolingLevel', 1, 1, 0, 0),
    (115, 17, 1, 'trainability', 5, 0.5, 0, 0),
    (124, 18, 1, 'catFriendly', 1, 1, 1, 0),
    (129, 19, 1, 'dogFriendly', 1, 1, 0, 0),
    (134, 20, 1, 'trainability', 1, 1, 0, 0);