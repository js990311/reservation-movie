import json
import csv
import os

def extract_k6_metrics(json_file, category):
    with open(json_file, 'r', encoding='utf-8') as f:
        try:
            data = json.load(f)
        except Exception:
            return None
    
    metrics = data.get('metrics', {})
    if not metrics:
        return None

    def get_val(metric_name, field):
        return metrics.get(metric_name, {}).get(field, 0)

    filename = os.path.basename(json_file)
    execution_type = 'WARMUP' if 'warmup' in filename.lower() else 'TEST'

    # 데이터 추출
    row = {
        '파일명': filename,
        '구분': category,  
        '유형': execution_type,
        '총요청수': int(get_val('http_reqs', 'count')),
        '실제RPS': round(get_val('http_reqs', 'rate'), 2),
        '누락율(dropped/s)': round(get_val('dropped_iterations', 'rate'), 2),
        '평균_duration(ms)': round(get_val('http_req_duration', 'avg'), 2),
        'p95_duration(ms)': round(get_val('http_req_duration', 'p(95)'), 2),
        '평균 waiting(ms)': round(get_val('http_req_waiting', 'avg'), 2),
        'p95 waiting(ms)': round(get_val('http_req_waiting', 'p(95)'), 2),
        '최대VU': int(get_val('vus_max', 'value')),
    }
    
    return row

def main():
    base_folder = './reports'
    output_file = './reports/performance_comparison_final.csv'
    
    # 비교할 타겟 폴더 정의
    categories = ['identity', 'jdbc', 'tsid']
    results = []

    for cat in categories:
        target_path = os.path.join(base_folder, cat)
        if not os.path.exists(target_path):
            print(f"알림: '{target_path}' 폴더가 없어 건너뜁니다.")
            continue

        for file in os.listdir(target_path):
            if file.endswith('.json'):
                res = extract_k6_metrics(os.path.join(target_path, file), cat.upper())
                if res:
                    results.append(res)
                    print(f"분석 완료 [{cat.upper()}]: {file}")

    if not results:
        print("분석할 유효한 JSON 파일이 없습니다. reports/before 및 reports/after 폴더를 확인하세요.")
        return

    # CSV 쓰기
    with open(output_file, 'w', newline='', encoding='utf-8-sig') as f:
        writer = csv.DictWriter(f, fieldnames=results[0].keys())
        writer.writeheader()
        writer.writerows(results)
    
    print(f"\n[최종 성공] '{output_file}' 생성 완료! 엑셀에서 Before/After를 비교해 보세요.")

if __name__ == "__main__":
    main()