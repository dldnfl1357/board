#!/bin/bash

echo "======================================"
echo "Board 프로젝트 테스트 실행"
echo "======================================"
echo ""

# 1. 테스트 실행
echo "1. 테스트 실행 중..."
./gradlew clean test --info

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ 테스트 실패!"
    exit 1
fi

echo ""
echo "✅ 테스트 성공!"
echo ""

# 2. API 문서 생성
echo "2. API 문서 생성 중..."
./gradlew asciidoctor

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ 문서 생성 실패!"
    exit 1
fi

echo ""
echo "✅ API 문서 생성 완료!"
echo ""

# 3. 결과 확인
echo "======================================"
echo "생성된 파일"
echo "======================================"
echo ""

if [ -d "build/generated-snippets" ]; then
    echo "📁 REST Docs Snippets:"
    ls -la build/generated-snippets/auth/
    echo ""
fi

if [ -d "build/docs/asciidoc" ]; then
    echo "📄 HTML 문서:"
    ls -lh build/docs/asciidoc/index.html
    echo ""
fi

echo "======================================"
echo "테스트 및 문서 생성 완료!"
echo "======================================"
echo ""
echo "생성된 문서 위치:"
echo "  - HTML: build/docs/asciidoc/index.html"
echo "  - Snippets: build/generated-snippets/"
echo ""
echo "문서 보기:"
echo "  - 브라우저에서 build/docs/asciidoc/index.html 파일을 열어보세요"
echo "  - 또는 애플리케이션 실행 후 http://localhost:8080/docs/index.html 접속"
echo ""
