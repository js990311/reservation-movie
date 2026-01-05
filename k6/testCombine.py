import json
import csv
import os

def extract_k6_metrics(json_file):
    with open(json_file, 'r', encoding='utf-8') as f:
        try:
            data = json.load(f)
        except Exception:
            return None
    
    metrics = data.get('metrics', {})
    if not metrics:
        return None

    # 주신 JSON 구조(values 키 없음)에 맞춘 직접 추출 함수
    def get_val(metric_name, field):
        # metrics -> http_reqs -> count 방식으로 바로 접근
        return metrics.get(metric_name, {}).get(field, 0)

    filename = os.path.basename(json_file)
    
    # 구분 로직: 파일명에 'Seat'이 있으면 신버전, 없으면 구버전
    test_type = "신버전(Seat락)" if "Seat" in filename or "seat" in filename else "구버전(Screening락)"

    # 데이터 추출
    row = {
        '파일명': filename,
        '구분': test_type,
        '총요청수': int(get_val('http_reqs', 'count')),
        '실제RPS': round(get_val('http_reqs', 'rate'), 2),
        '누락율(dropped/s)': round(get_val('dropped_iterations', 'rate'), 2),
        '예매성공': int(get_val('reservation_success', 'count')),
        '예매실패': int(get_val('reservation_fail', 'count')),
        'p95_응답시간(ms)': round(get_val('http_req_duration', 'p(95)'), 2),
        'p95_대기시간(ms)': round(get_val('http_req_waiting', 'p(95)'), 2),
        '최대VU': int(get_val('vus_max', 'value')),
        '좌석풀_크기': len(data.get('setup_data', {}).get('seatPool', []))
    }
    
    return row

def main():
    input_folder = './reports' # JSON 파일들이 있는 폴더명
    output_file = './reports/performance_comparison_final.csv'
    
    if not os.path.exists(input_folder):
        print(f"오류: '{input_folder}' 폴더를 생성하고 JSON 파일들을 넣어주세요.")
        return

    results = []
    for file in os.listdir(input_folder):
        if file.endswith('.json'):
            res = extract_k6_metrics(os.path.join(input_folder, file))
            if res:
                results.append(res)
                print(f"분석 완료: {file} (RPS: {res['실제RPS']})")

    if not results:
        print("분석할 유효한 JSON 파일이 없습니다.")
        return

    # CSV 쓰기 (엑셀 한글 깨짐 방지용 utf-8-sig)
    with open(output_file, 'w', newline='', encoding='utf-8-sig') as f:
        writer = csv.DictWriter(f, fieldnames=results[0].keys())
        writer.writeheader()
        writer.writerows(results)
    
    print(f"\n[최종 성공] '{output_file}' 파일이 생성되었습니다. 엑셀에서 확인하세요!")

if __name__ == "__main__":
    main()