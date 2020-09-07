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

    public static NValid of(Map<String, Object> params) {
        return new NValid(params);
    }

    private NValid(Map<String, Object> params) {
        this.params = params;
    }

    public NValid key(String... keyName) {
        this.keyName = keyName;
        this.requireMode = false;
        return this;
    }

    public NValid requireKey(String... keyName) {
        this.keyName = keyName;
        this.requireMode = true;
        return notEmpty();
    }

    public NValid defaultKey(String keyName, Object defaultValue) {
        String[] k = { keyName };
        this.keyName = k;
        this.requireMode = true;
        return emptyDefault(defaultValue);
    }

    private void validate(boolean isValid) {
        if (isValid) {
            logger.debug("Vaild OK");
        } else {
            logger.debug("Vaild FAIL");
            throw new BadRequestException("4000", "파라미터 검증 오류");
        }
    }

    public NValid emptyDefault(Object defaultValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                params.put(k, defaultValue);
            }
        }
        return this;
    }

    public NValid notEmpty() {
        checkMapAndKey();

        for (String k : keyName) {
            validate(params != null && params.get(k) != null);
        }

        return this;
    }

    public NValid eq(Object equalsValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    return this;
                }
                validate(false);
                return this;
            }

            validate(params.get(k).equals(equalsValue));
        }
        return this;
    }

    public NValid notEq(Object notEqualsValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    return this;
                }
                validate(false);
                return this;
            }

            validate(!(params.get(k).equals(notEqualsValue)));
        }
        return this;
    }

    public NValid optional(Predicate<Object> pre) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    return this;
                }
                validate(false);
                return this;
            }

            validate(pre.test(params.get(k)));
        }
        return this;
    }

    public NValid in(Object... containsValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    return this;
                }
                validate(false);
                return this;
            }

            boolean isContain = false;
            Object value = params.get(k);
            for (Object containValue : containsValue) {
                if (value.equals(containValue)) {
                    isContain = true;
                    break;
                }
            }

            validate(isContain);
        }
        return this;
    }

    public NValid max(Number maxValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    return this;
                }
                validate(false);
                return this;
            }

            boolean bool = true;
            try {
                Object value = params.get(k);
                if (value instanceof Integer) {
                    bool = Integer.parseInt(maxValue.toString()) >= Integer.parseInt(params.get(k).toString());
                } else if (value instanceof Long) {
                    bool = Long.parseLong(maxValue.toString()) >= Long.parseLong(params.get(k).toString());
                }
                if (value instanceof Double) {
                    bool = Double.parseDouble(maxValue.toString()) >= Double.parseDouble(params.get(k).toString());
                }
                if (value instanceof Float) {
                    bool = Float.parseFloat(maxValue.toString()) >= Float.parseFloat(params.get(k).toString());
                }
            } catch (NumberFormatException e) {
                bool = false;
            }
            validate(bool);
        }
        return this;
    }

    public NValid min(Number minValue) {
        checkMapAndKey();

        for (String k : keyName) {
            if (!params.containsKey(k)) {
                if (!requireMode) {
                    logger.debug("Skip :: {}", k);
                    return this;
                }
                validate(false);
                return this;
            }

            boolean bool = true;
            try {
                Object value = params.get(k);
                if (value instanceof Integer) {
                    bool = Integer.parseInt(minValue.toString()) <= Integer.parseInt(params.get(k).toString());
                } else if (value instanceof Long) {
                    bool = Long.parseLong(minValue.toString()) <= Long.parseLong(params.get(k).toString());
                }
                if (value instanceof Double) {
                    bool = Double.parseDouble(minValue.toString()) <= Double.parseDouble(params.get(k).toString());
                }
                if (value instanceof Float) {
                    bool = Float.parseFloat(minValue.toString()) <= Float.parseFloat(params.get(k).toString());
                }
            } catch (NumberFormatException e) {
                bool = false;
            }
            validate(bool);
        }
        return this;
    }

    private void checkMapAndKey() {
        if (params == null || keyName == null) {
            throw new UnsupportedOperationException("The key is required.");
        }
    }
}
