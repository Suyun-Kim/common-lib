package com.nx.lib.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nx.lib.exception.BadRequestException;

public class NValid {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String[] keyName;
    private Map<String, Object> params;
    private boolean requireMode = false;

    /**
     * <p>
     * NValid를 초기화
     * </p>
     * 
     * <pre>
     * #Usecase
     * 
     * NValid.of(map)
     *      .key("name");
     *      
     * NValid.of(map)
     *      .key("name", "age");
     * 
     * NValid.of(map)
     *      .requireKey("name");
     * 
     * NValid.of(map)
     *      .key("name")
     *      .eq("babo")
     *      .key("age")
     *      .max("30")
     *      .min("18")
     *      .key("skills")
     *      .optional(obj -> {
     *          List<Map<String,Object>> skills = (List<Map<String,Object>>) obj;
     *          return skills.size() > 10
     *      });
     *
     * </pre>
     */
    public static NValid of(Map<String, Object> params) {
        return new NValid(params);
    }

    private NValid(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * <p>
     * 데이터로 사용하나, 필수가 아닌 파라미터 이름을 선택
     * </p>
     * <p>
     * - Arrays로 파라미터를 입력 시 동일한 검증 처리
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key("name");
     *      
     * #Example2
     * 
     * NValid.of(map)
     *      .key("name", "age", "school" ....);
     *
     * </pre>
     */
    public NValid key(String... keyName) {
        this.keyName = keyName;
        this.requireMode = false;
        return this;
    }

    /**
     * <p>
     * 필수로 포함되어야 할 파라미터 이름을 선택
     * </p>
     * <p>
     * - "" 공백의 경우 포함된 파라미터로 허용 <br>
     * - Arrays로 파라미터를 입력 시 동일한 검증 처리
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .requireKey("name");
     *      
     * #Example2
     * 
     * NValid.of(map)
     *      .requireKey("name", "age", "school" ....);
     *
     * </pre>
     */
    public NValid requireKey(String... keyName) {
        this.keyName = keyName;
        this.requireMode = true;
        return notNull();
    }

    /**
     * <p>
     * 비어있는 Key일 경우 Value 넣어줌
     * </p>
     * <p>
     * put, putIfEmpty 로 대체한다.
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .defaultKey("name", "park");
     * 
     * </pre>
     */
    @Deprecated
    public NValid defaultKey(String keyName, Object defaultValue) {
        checkMap();

        if (!params.containsKey(keyName) || "".equals(params.get(keyName)) || params.get(keyName) == null) {
            params.put(keyName, defaultValue);
        }

        return this;
    }

    /**
     * <p>
     * 비어있는 Key일 경우 Value 넣어줌
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .putIfEmpty("name", "park");
     *
     * </pre>
     */
    public NValid putIfEmpty(String keyName, Object value) {
        checkMap();

        if (!params.containsKey(keyName) || "".equals(params.get(keyName)) || params.get(keyName) == null) {
            params.put(keyName, value);
        }

        return this;
    }

    /**
     * <p>
     * Key에 Value 강제로 넣어줌
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .put("name", "park");
     *
     * </pre>
     */
    public NValid put(String keyName, Object value) {
        checkMap();

        params.put(keyName, value);
        return this;
    }

    private void validate(boolean isValid, String key, String reason) {
        if (!isValid) {
            logger.error("Vaild FAIL");
            throw new BadRequestException("4000", "파라미터 검증 오류\n[Key] : " + key + ", 원인 : : " + reason);
        }
    }

    private void validate(boolean isValid, String reason) {
        if (!isValid) {
            logger.error("Vaild FAIL");
            throw new BadRequestException("4000", "파라미터 검증 오류\n 원인 : : " + reason);
        }
    }

    /**
     * <p>
     * 파라미터의 default값을 설정
     * </p>
     * <p>
     * - key() 또는 requireKey()로 key의 설정 선행 필요<br>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key("name")
     *      .emptyThenDefault("msseol");
     *
     * </pre>
     */
    public NValid emptyThenDefault(Object defaultValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k) || "".equals(params.get(k)) || params.get(k) == null) {
                params.put(k, defaultValue);
            }
        }
        return this;
    }

    /**
     * <p>
     * 파라미터가 Null 또는 공백인지 체크한다.
     * </p>
     * <p>
     * - key() 또는 requireKey()로 key의 설정 선행 필요<br>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .notEmpty();
     *
     * </pre>
     */
    public NValid notEmpty() {
        checkMapAndKey();

        for (String k : keyName) {
            validate(params != null && params.containsKey(k) && params.get(k) != null && !"".equals(params.get(k)), k,
                    "값이 없거나 공백입니다.");
        }

        return this;
    }

    /**
     * <p>
     * 파라미터가 Null인지 체크
     * </p>
     * <p>
     * - "" 공백의 경우 포함된 파라미터로 허용<br>
     * - requireKey() 사용 시 key() + notNull()<br>
     * - key() 또는 requireKey()로 key의 설정 선행 필요<br>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .notNull();
     *
     * </pre>
     */
    public NValid notNull() {
        checkMapAndKey();

        for (String k : keyName) {
            validate(params != null && params.containsKey(k) && params.get(k) != null, k, "값이 없습니다.");
        }

        return this;
    }

    /**
     * <p>
     * 파라미터가 해당하는 값과 같은 값인지 체크
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .eq("0");
     *
     * </pre>
     */
    public NValid eq(Object equalsValue) {
        checkMapAndKey();

        String target = equalsValue.toString();
        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            String value = String.valueOf(params.get(k));
            validate(value.equals(target), k, equalsValue + "와 값이 일치해야 합니다.");
        }
        return this;
    }

    /**
     * <p>
     * 파라미터가 해당하는 값일 경우 검증 실패 처리
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .notEq("0");
     *
     * </pre>
     */
    public NValid notEq(Object notEqualsValue) {
        checkMapAndKey();

        String target = notEqualsValue.toString();
        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            String value = String.valueOf(params.get(k));
            validate(!(value.equals(target)), k, notEqualsValue + "가 아닌 값이어야 합니다.");
        }
        return this;
    }

    /**
     * <p>
     * Param을 커스텀한 조건에 따라 검증
     * </p>
     * <p>
     * - 값에 대해 복잡하게 검증할 때 사용<br>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .optional(val -> ("custom".equals(val) && "custom2".equals(val));
     *
     * </pre>
     */
    public NValid optional(Predicate<Object> pre) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            validate(pre.test(params.get(k)), k, "Predicate 검증 실패.");
        }
        return this;
    }

    /**
     * <p>
     * Param Map을 커스텀한 조건에 따라 검증
     * </p>
     * <p>
     * - Map 자체를 복잡하게 검증할 때 사용<br>
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .optionalMap(map -> (Boolean.valueOf(map.get("b1")) && Boolean.valueOf(map.get("b2"))));
     * 
     * </pre>
     * 
     */
    public NValid optionalMap(Predicate<Map<String, Object>> paramMapPredicate) {
        checkMap();

        boolean valid = paramMapPredicate.test(params);

        validate(valid, "검증 로직 실패");
        return this;
    }

    /**
     * <p>
     * 글자수 길이 체크 n~n
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .length(0, 60);
     *
     * </pre>
     */
    public NValid length(int minLen, int maxLen) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            String str = params.getOrDefault(k, "").toString();
            validate(minLen <= str.length() && str.length() <= maxLen, k,
                    "글자 수 검증 실패.[" + minLen + " ~ " + maxLen + "]");
        }
        return this;
    }

    /**
     * <p>
     * 파라미터를 설정된 값들에 포함되는지 체크한다.
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .in("a", "b", "c");
     * 
     * </pre>
     */
    public NValid in(Object... containsValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            boolean isContain = false;
            String value = String.valueOf(params.get(k));
            for (Object containValue : containsValue) {
                if (value.equals(containValue.toString())) {
                    isContain = true;
                    break;
                }
            }

            validate(isContain, k, "포함하는 값이 없습니다.");
        }
        return this;
    }

    /**
     * <p>
     * 숫자형 파라미터가 최대수치 범위 내인지 체크
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크<br>
     * - Number Type이 아니면 검증 실패
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .max(100);
     * 
     * </pre>
     * 
     */
    public NValid max(int maxValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            boolean bool = true;
            try {
                bool = maxValue >= Integer.parseInt(String.valueOf(params.get(k)));
            } catch (NumberFormatException e) {
                bool = false;
            }
            validate(bool, k, maxValue + "값을 초과하거나 정수 타입이 아닙니다.");
        }
        return this;
    }

    /**
     * <p>
     * 숫자형 파라미터가 최소수치 범위 내인지 체크
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크<br>
     * - Number Type이 아니면 검증 실패
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .min(30);
     * 
     * </pre>
     * 
     */
    public NValid min(int minValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            boolean bool = true;
            try {
                bool = minValue <= Integer.parseInt(String.valueOf(params.get(k)));
            } catch (NumberFormatException e) {
                bool = false;
            }
            validate(bool, k, minValue + "값 미만이거나 숫자 타입이 아닙니다.");
        }
        return this;
    }

    /**
     * <p>
     * 숫자형 파라미터가 범위 내인지 체크
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크<br>
     * - Number Type이 아니면 검증 실패
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .between(1,30);
     * 
     * </pre>
     * 
     */
    public NValid between(int minValue, int maxValue) {
        if (minValue >= maxValue) {
            return this;
        }
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            boolean bool = true;
            try {
                int val = Integer.parseInt(String.valueOf(params.get(k)));
                bool = minValue <= val && maxValue >= val;
            } catch (NumberFormatException e) {
                bool = false;
            }
            validate(bool, k, minValue + "~" + maxValue + " 사이 값이 아니거나 숫자 타입이 아닙니다.");
        }
        return this;
    }

    /**
     * <p>
     * 파라미터가 해당하는 Date 포맷인지 조회
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 체크
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .date("0");
     *
     * </pre>
     */
    public NValid date(String strFormat) {
        checkMapAndKey();

        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    continue;
                }
                validate(false, k, "값이 없습니다.");
                return this;
            }

            boolean valid = true;
            try {
                String dateStr = String.valueOf(params.get(k));
                sdf.setLenient(false);

                sdf.parse(dateStr);
            } catch (ParseException e) {
                valid = false;
            }

            validate(valid, k, "날짜 형식(" + strFormat + ")이 아닙니다.");
        }
        return this;
    }

    /**
     * <p>
     * 해당 키/값에 대해 Function Retrun 값으로 변경, 대체값으로 변경됨
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 적용<br>
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .replace(value -> true, replaceValue);
     * 
     * </pre>
     * 
     */
    public NValid replace(Function<Object, Object> replaceFunction) {
        checkMapAndKey();

        for (String k : keyName) {
            params.replace(k, replaceFunction.apply(params.get(k)));
        }

        return this;
    }

    /**
     * <p>
     * 해당 키/값에 대해 조건부로 값 변경, 조건에 맞을 경우 대체값으로 변경됨
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 적용<br>
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey)
     *      .replace(value -> true, replaceValue);
     * 
     * </pre>
     * 
     */
    public NValid replace(Predicate<Object> condition, Object replaceValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (condition.test(params.get(k))) {
                params.replace(k, replaceValue);
            }
        }
        return this;
    }

    /**
     * <p>
     * Integer 값인 키들에 대해 Order순인지 체크
     * </p>
     * <p>
     * - Arrays 형태로 파라미터를 설정했을 경우 전부 적용<br>
     * </p>
     * 
     * <pre>
     * #Example
     * 
     * NValid.of(map)
     *      .key(mapKey1, mapKey2, mapKey3)
     *      .compareOrder(Order.ASC);
     * 
     * </pre>
     * 
     */
    public NValid compareOrder(Order order) {
        checkMapAndKey();
        Integer beforeValue = null;
        for (String k : keyName) {
            try {
                if (params.containsKey(k) && params.get(k) != null) {
                    int value = Integer.parseInt(String.valueOf(params.get(k)));
                    if (value == 0) {
                        continue; // 0 인 값 무시
                    }
                    if (beforeValue == null) {
                        beforeValue = value;
                        continue;
                    }

                    if (order == Order.ASC) {
                        if (beforeValue > value) {
                            validate(false, k, k + " << 이전 키값보다 더 작습니다. ");
                        }
                    } else if (order == Order.DESC) {
                        if (beforeValue < value) {
                            validate(false, k, k + " << 이전 키값보다 더 큽니다. ");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                validate(false, k, "숫자 형식(" + params.get(k) + ")이 아닙니다.");
                return this;
            }
        }

        return this;
    }

    private void checkMap() {
        if (params == null) {
            throw new UnsupportedOperationException("The params map is required.");
        }
    }

    private void checkMapAndKey() {
        if (params == null || keyName == null) {
            throw new UnsupportedOperationException("The key is required.");
        }
    }

    public enum Order {
        ASC, DESC
    }
}
