import type { NextConfig } from "next";

const nextConfig: NextConfig = {
    // TypeScript 에러는 여전히 여기서 무시 가능합니다.
    typescript: {
        ignoreBuildErrors: true,
    },
    // 이미지 등 기타 설정이 있다면 아래 유지
    images: {
        remotePatterns: [{ protocol: 'https', hostname: '**' }],
    },
};

export default nextConfig;