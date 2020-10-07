package com.nx.lib.api.util;

import java.util.Map;
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
     * 기본값이 필요한 파라미터의 default값을 설정
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
    public NValid defaultKey(String keyName, Object defaultValue) {
        String[] k = { keyName };
        this.keyName = k;
        this.requireMode = true;
        return emptyThenDefault(defaultValue);
    }

    private void validate(boolean isValid, String key, String reason) {
        if (!isValid) {
            logger.error("Vaild FAIL");
            throw new BadRequestException("4000", "파라미터 검증 오류\n[Key] : " + key + ", [Reason] : " + reason);
        } else {
            logger.debug("Vaild OK");
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

            String value = params.get(k).toString();
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

            String value = params.get(k).toString();
            validate(!(value.equals(target)), k, notEqualsValue + "가 아닌 값이어야 합니다.");
        }
        return this;
    }

    /**
     * <p>
     * 파라미터를 별도의 조건 Function을 통해 검증 처리
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

            String str = params.get(k).toString();
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
            String value = params.get(k).toString();
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
                bool = maxValue >= Integer.parseInt(params.get(k).toString());
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
                bool = minValue <= Integer.parseInt(params.get(k).toString());
            } catch (NumberFormatException e) {
                bool = false;
            }
            validate(bool, k, minValue + "값 미만이거나 숫자 타입이 아닙니다.");
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
            if (params.containsKey(k) && params.get(k) != null) {
                if (condition.test(params.get(k))) {
                    params.put(k, replaceValue);
                }
            }
        }
        return this;
    }

    private void checkMapAndKey() {
        if (params == null || keyName == null) {
            throw new UnsupportedOperationException("The key is required.");
        }
    }
}
