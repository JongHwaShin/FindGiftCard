# DataIO 코드 설명

'''java
    private boolean isDataLoadFin = false;

'''
    인터넷 등을 통한 데이터를 받아 올 시, 메인 스레드로는 해당 작업을 할 수 없다.
    따라서 다른 별도의 스레드를 형성하여 데이터를 받아오는데, 
    이때 이 스레드의 데이터 로딩 작업이 끝났음을 알리기 위한 boolean 값이다.

#### private String reUnionStr(String strings, int idx)

'''java
    // 띄워쓰기 단위로 분할 후 idx만큼 다시 재조립
    private String reUnionStr(String strings, int idx){
        String result = "";
        String [] string_split = strings.split(" ");
        if(string_split.length < idx){
            return strings;
        }else {
            for(int i = 0; i < idx; i++) {
                result = result + string_split[i] + " ";
            }
            return result;
        }
    }

'''

    사용예시 : 
        String text = "경남 창원시 진해구 석동 543-5 롯데마트 진해점";
        
        String textAlt = (text, 5);
        textAlt >>>> "경남 창원시 진해구 석동 543-5 "

        String textAlt = (text, 2);
        textAlt >>>> "경남 창원시 "

'''java

#### private String parser(String url)

    위 함수는 <ttps://digitalbourgeois.tistory.com/57/> [IT 글자국] 을 참고했다.



#### private String sql_parser(String url)

// mysql 데이터 파싱
    private String sql_parser(String url){
        try {
            Log.d("jsuo API", url);
            Document doc = Jsoup.connect(url).get();

            String text = "";

            text = doc.body().getElementsByTag("pre").text();

            return text;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
'''

    url 주소로 가서 pre 태그의 text를 긁어온다.
    API 형식으로 키워드로 SQL문을 보네면 그 결과를 pre 태그에 보내고, 그 정보를 읽어오는 것 이다.
    pre 태그의 텍스트들은 String 형식으로 반환된다.

#### private ArrayList<ArrayList<String>> getTable(String table)

    위 함수는 길기 때문에 끊어서 설명하겠다.

'''java
    ArrayList<ArrayList<String>> zero = new ArrayList<ArrayList<String>>();

        // 데이터 수 파악
        String maxIDX = sql_parser(url_sql + "SELECT MAX(idx) FROM " + table + ";");
        int maxIdx = Integer.valueOf(maxIDX.split(",")[1]);

        // 컬럼 수 파악
        String cout_col = sql_parser(url_sql + "SELECT * FROM " + table + " WHERE idx < 2;");
        int colIdx = cout_col.split("\n").length;

        for(int i = 0; i < colIdx; i++) {
            ArrayList<String> temp = new ArrayList<String>();
            temp.add(cout_col.split("\n")[i].split(",")[0]);
            zero.add(temp);
        }
'''
    먼저 가져오려는 데이터의 컬럼수를 확인한다.
    그이유는 아래에서도 설명하겠지만 한번에 많은 텍스트를 파싱하게 되면
    중간에 데이터가 끊겨서 받게 된다.
    따라서 1000 row씩 끊어서 가져오도록 하기 위해서 일단 총 컬럼수를 알아온다.
    그다음 column의 갯수만큼 어레이 리스트 객체를 생성하여 이중 어레이리스트에 넣는다.

'''java

    // 한번에 하면 좋겠지만 한번에 다 배열에 저장 시 에러나서 1000번씩 끊어 넣음
        for(int i = 0; i < maxIdx / 1000; i++){

            String sql = url_sql + "SELECT * FROM " + table + " " +
                    "WHERE idx >= " + (1000 * i) + " " +
                    "AND idx < " + (1000 * (i + 1));

            String zeroPayData = sql_parser(sql);
            String [] col = zeroPayData.split("\n");
            for(int j =0; j < col.length; j++) {
                String [] temp = col[j].split(",");
                for(int k = 1; k < temp.length; k++) {
                    zero.get(j).add(temp[k]);
                }
            }
        }

        String sql = url_sql + "SELECT * FROM " + table + " " +
                "WHERE idx >= " + (1000 * (maxIdx / 1000)) +  " " +
                "AND idx <= " + maxIdx;
        Log.d("ZeropayMobile", sql);

        String zeroPayData = sql_parser(sql);
        String [] col = zeroPayData.split("\n");
        for(int j =0; j < col.length; j++) {
            String [] temp = col[j].split(",");
            for(int k = 1; k < temp.length; k++) {
                zero.get(j).add(temp[k]);
            }
        }

        return zero;

'''
    위에서 설명한대로 1000 row씩 끊어서 데이터를 가져온다.
    서버에서는 데이터를 각 컬럼별로 한줄씩 나타내고 "," 으로 각 데이터를 구분한다.
    ex)
        idx, 1, 2, 3, 4,
        name, qwer, qwert, qwerty, qwertyu
        pw, 123, 321, 132, 142

    위처럼 받은 String을 split으로 잘라서 순서대로 ArrayList에 집어 넣는다.

    위 작업들이 끝나면, ArrayList를 반환한다.

#### private void LocationUpdate(ArrayList<ArrayList<String>> data, String tablename)

    이 함수또한 복잡하여 끊어 설명하고자 한다.
    이 함수의 목적은 내 데스크탑에 열어둔 서버에 Update 문을 만들기 위해서 이다.

<img src="./../../../img/uml/LocationUpdate.png"/>
    
'''java

    int control_str_length = 6;
    String update_query = "UPDATE " + tablename + " SET %0A";
    String lat_condition = "";
    String lng_condition = "";
    String WHERE_condition = " WHERE idx > 0;";

'''
    control_str_length 는 reUnionStr에서 자를 텍스트 수를 제어하기 위한 값이다.
    도로명 주소 API를 검색할 때 keyword 문자 길이가 너무 길어도 값이 안나오고, 너무 짧아도 값이 안나왔다. 그러나 도로명 주소의 규칙 상 보통 5 ~ 6단위로 끊어서 검색하면 대체로 정확한 위치가 출력되었다. 따라서 초기값은 6 이후 for문을 돌면서 -1 씩 차감하여 검색하는 방식이다.
    update_query 는 UPDATE 문을 작성한다. %0A는 url 에서 엔터를 의미하는 문자이다. (프로그레밍 코드에서 대체로 엔터가 \n인것 처럼)


###### for(int i = 1; i < data.get(0).size(); i++)

    위에서 gatTable 함수로 받은 모든 데이터들에 대하여 1회씩 돌린다.
    i 가 1부터인 이유는 0번째는 데이터 값이 아닌 column의 명칭이기 때문이다.

''' java

        double lat = Double.valueOf(data.get(5).get(i));
        double lng = Double.valueOf(data.get(6).get(i));

        if(!data.get(5).get(i).equals("1000") && !data.get(5).get(i).equals("1000")){
            continue;
        }

        try {
            sleep(300);
        }catch (InterruptedException e){

        }

'''

    i번째 row 에서 위도와 경도를 가져오고, 만약 그 값이 1000(실제 불가능한 값)이 아니면 이 row는 생략한다. 

'''java

        String keywords = reUnionStr(data.get(1).get(i), control_str_length); // #1
        Log.d("juso API", "idx : " + i + " keyword : " + keywords);

        String admCd = "&admCd=";           // 행정구역코드
        String rnMgtSn = "&rnMgtSn=";       // 도로명코드
        String udrtYn = "&udrtYn=";         // 지하여부
        String buldMnnm = "&buldMnnm=";   // 건물본번
        String buldSlno = "&buldSlno=";     // 건물부번
        String entX = "x=";                 // x좌표
        String entY = "y=";                 // y좌표

        String json = parser(jusoStreetNameApi + keywords); // #2
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject results = jsonObject.getJSONObject("results");
            String jusoStr = results.getString("juso");
            JSONArray jusos = new JSONArray(jusoStr);
            JSONObject juso = jusos.getJSONObject(0);
            admCd = admCd + juso.getString("admCd");
            rnMgtSn = rnMgtSn + juso.getString("rnMgtSn");
            udrtYn = udrtYn + juso.getString("udrtYn");
            buldMnnm = buldMnnm + juso.getString("buldMnnm");
            buldSlno = buldSlno + juso.getString("buldSlno"); // #3

            control_str_length = 6; // #4

        }catch (JSONException e){
            Log.d("juso Api", "can not find juso idx : " + control_str_length + " keywords : " + keywords);

            if(control_str_length >= 4) {
                i = i - 1;
                control_str_length = control_str_length - 1;
            }
            continue; // #5
        }

'''

    먼저 i 번째 도로명 주소 전체 중에서 띄워쓰기단위로 control_str_length값만큼 자른다. // #1
    그리고 그 값을 도로명주소 API에 보네어 데이터를 JSON 형태(의 스트링)로 받는다. // #2
    받아온 JSON 데이터를 통해 좌표제공 API 가 요구하는 값을 저장한다. // #3
    데이터 저장을 성공하면 control_str_length을 초기화한다. // #4
    만약 데이터를 받아오는데 실패하였으면, control_str_length을 -1 하고 다시 실행한다. 이 과정이 3번이상 실행되었으면 그냥 그 row는 스킵한다. // #5

'''java

            String LocJson = parser(jusoLocationApi + admCd + rnMgtSn + udrtYn + buldMnnm + buldSlno); // #1
            try {
                JSONObject jsonObject = new JSONObject(LocJson);
                JSONObject results = jsonObject.getJSONObject("results");
                String jusoStr = results.getString("juso");
                JSONArray jusos = new JSONArray(jusoStr);
                JSONObject juso = jusos.getJSONObject(0);

                entX = entX + juso.getString("entX");
                entY = entY + juso.getString("entY");

            }catch (JSONException e){
                Log.d("juso API", "can not find Location keywords : " + keywords);
                continue; // #2
            }
            //Log.d("juso API", "entX : " + entX + " entY : " + entY);

            String getLoc = sql_parser(new KEY().getMYNodeServer("loc") + entX + "&" + entY); // #3


            if(getLoc == null){
                Log.d("juso API", "can not get Location");
                continue; 
            }
            if(getLoc.split(",")[1] == null || getLoc.split(",")[0] == null){
                Log.d("juso API", "can not get Location");
                continue;
            }

            data.get(5).set(i, getLoc.split(",")[1]);
            data.get(6).set(i, getLoc.split(",")[0]);

            lat = Double.valueOf(getLoc.split(",")[1]);
            lng = Double.valueOf(getLoc.split(",")[0]);



            lat_condition = " WHEN " + i + " THEN " + lat + " %0A ";
            sql_parser(new KEY().getMYNodeServer("lat") + lat_condition);
            lng_condition = " WHEN " + i + " THEN " + lng + " %0A ";
            sql_parser(new KEY().getMYNodeServer("lng") + lng_condition); // #4

'''

    위에서 받은 데이터로 해당 도로명 주소의 좌표값을 JSON 형태로 받아온다. // #1
    만약 실패시 그 회차는 스킵한다. // #2
    받아온 x, y 좌표값을 Proj4j 프로그렘을 통하여 위도와 경도값으로 변환하여 받아온다. // #3
    변환된 값을 내 데스크 탑 서버에 쿼리문으로 변환하여 추가한다. // #4
    
    
    이후는 다음행 반복이다.
    
