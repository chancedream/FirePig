package my.framework.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class XString {
	
	private static final Logger logger = Logger.getLogger(XString.class);
	private static final XString NULL = new XString(null);
	
	public static XString from(String target) {
		return new XString(target);
	}
	
	public static boolean isEmpty(String... targets) {
		for (String target : targets) {
			if (target != null && !target.isEmpty()) return false;
		}
		return true;
	}
	
	public static String join(Object[] target, String sep) {
	    String result = "";
	    for (int i = 0; i < target.length; i++) {
	        if (i != 0) {
	            result += sep;
	        }
	        result += target[i].toString();
	    }
	    return result;
	}
	
	public static boolean notEmpty(String... targets) {
		for (String target : targets) {
			if (target == null || target.isEmpty()) return false;
		}
		return true;
	}
	
	public static int sim(String s1, String s2) {
		int[] r = new int[s2.length()];
		for (int j = 0; j < s1.length(); j++) {
			for (int i = 0; i < s2.length(); i++) {
				if (s1.charAt(j) == s2.charAt(i)) {
					r[i] = (i == 0 ? 1 : r[i - 1] + 1); 
				}
			}
			int t = 0;
			for (int k = 0; k < r.length; k++) {
				t = Math.max(t, r[k]);
				r[k] = t;
			}			
		}		
		return r[r.length - 1];
	}
	
	private String target;
	
	public XString(String target) {
		this.target = target;
	}
	
	public XString camelize() {
		if (target == null) return NULL;
		return new XString(Character.toLowerCase(target.charAt(0)) + target.substring(1));
	}
	
	public XString capitalize() {
		if (target == null) return NULL;
		return new XString(Character.toUpperCase(target.charAt(0)) + target.substring(1));
	}
	
	public boolean contains(CharSequence str) {
	    if (target == null) return false;
	    return target.contains(str);
	}
	
//	@SuppressWarnings("unchecked")
//	public Object evaluate(Map context) {
//		return evaluate(context, null);
//	}
//	
//	@SuppressWarnings("unchecked")
//	public Object evaluate(Map context, Object root) {
//		if (target == null) return null;
//		if (target.startsWith("ognl:")) {
//			String expression = target.substring(5);
//			try {
//				return Ognl.getValue(expression, context, root);
//		    } catch (OgnlException e) {
//		    	logger.warn("Error when evaluate OGNL expression: " + expression, e);
//		    } catch (Throwable e) {
//		    	logger.warn("Error when evaluate OGNL expression: " + expression);
//		    	throw new RuntimeException(e);
//		    }
//		}
//		return target;
//	}
//	
	public int indexOf(String str) {
	    if (target != null) {
	        return target.indexOf(str);
	    }
	    return -1;
	}
	
	public boolean isEmpty() {
	    return target == null || target.isEmpty();
	}
	
    public boolean isMatch(String regex) {
        if (target != null) {
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(target);
            if (matcher.find()) return true;
        }
        return false;
    }
    
    public boolean isNull() {
		return target == null;
	}
    
    public XString match(String regex) {
        if (target != null) {
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(target);
            if (matcher.find()) {
                return new XString(matcher.groupCount() >= 1 ? matcher.group(1) : matcher.group());             
            }
        }
        logger.warn(String.format("\"%s\" match \"%s\"", target, regex));
        return NULL;
    }
    
    public XString match(String regex, int group) {
        if (target != null) {
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(target);
            if (matcher.find()) {
                if (matcher.groupCount() >= group) {
                    return new XString(matcher.group(group));
                }
                else {
                    logger.warn(String.format("\"%s\" match \"%s\", but group index [%i] out of [%i]", target, regex, group, matcher.groupCount()));
                    return NULL;
                }
            }
        }
        logger.warn(String.format("\"%s\" match \"%s\"", target, regex));
        return NULL;
    }
    
    public XString match(String regex, int group, String defaultValue) {
        if (target != null) {
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(target);
            if (matcher.find()) {
                if (matcher.groupCount() >= group) {
                    return new XString(matcher.group(group));
                }
                else {
                    logger.warn(String.format("\"%s\" match \"%s\", but group index [%i] out of [%i]", target, regex, group, matcher.groupCount()));
                    return defaultValue == null ? NULL : new XString(defaultValue);
                }
            }
        }
        logger.warn(String.format("\"%s\" match \"%s\"", target, regex));
        return defaultValue == null ? NULL : new XString(defaultValue);
    }
    
	public XString match(String regex, String defaultValue) {
        if (target != null) {
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(target);
            if (matcher.find()) {
                return new XString(matcher.groupCount() >= 1 ? matcher.group(1) : matcher.group());             
            }
        }        
        return defaultValue == null ? NULL : new XString(defaultValue);
    }
	
	public List<XString> matchAll(String regex) {
	    List<XString> result = new ArrayList<XString>();
        if (target != null) {
            Pattern pattern = Pattern.compile(regex); 
            Matcher matcher = pattern.matcher(target);
            while (matcher.find()) {
                result.add(new XString(matcher.groupCount() >= 1 ? matcher.group(1) : matcher.group()));             
            }
        }
        if (result.isEmpty()) {
            logger.warn(String.format("\"%s\" match \"%s\"", target, regex));
        }
        return result;
	}
	
	public boolean notMatch(String regex) {
        return !isMatch(regex);
    }
	
	public XString remove(String regex) {
		return target == null ? NULL : new XString(target.replaceAll(regex, ""));
	}
	
    public XString replace(String regex, String s) {
		return target == null ? NULL : new XString(target.replaceAll(regex, s));
	}
    
    public String replaceAll(String regex, String replacement, int flag) {
	    return Pattern.compile(regex, flag).matcher(target).replaceAll(replacement);
	}
    
	public XString split(String regex, int index) {
        if (target != null) {
            String[] parts = target.split(regex);
            if (index < parts.length) {
                return new XString(parts[index]);
            }
        }
        logger.warn(String.format("\"%s\" split \"%s\", %d", target, regex, index));
        return NULL;
    }
	
	public boolean startsWith(String prefix) {
        return target != null ? target.startsWith(prefix) : false;
    }
	
	public XString substring(int beginIndex, int endIndex) {
		if (target != null) {
			int length = target.length();
			if (beginIndex < 0) beginIndex += length;
			if (endIndex < 0) endIndex += length;
			try {
				return new XString(target.substring(beginIndex, endIndex));
			} catch (StringIndexOutOfBoundsException e) {}
		}
		logger.warn(String.format("\"%s\" substring %d, %d", target, beginIndex, endIndex));
		return NULL;
	}
	
	public Date toDate(String format) {
		if (target != null) {
			try {
	            return new SimpleDateFormat(format).parse(target.trim());
	        } catch (ParseException e) {
	        	logger.warn(String.format("\"%s\" toDate \"%s\"", target, format), e);
	        }
		}
		return null;
	}
	
	public Date toDate(String format, Date defaultValue) {
		if (target != null) {
			try {
	            return new SimpleDateFormat(format).parse(target.trim());
	        } catch (ParseException e) {}
		}
		return defaultValue;
	}
	
	public float toFloat() {
		return toFloat(0f);
	}
	
    public float toFloat(float defaultValue) {
		if (target != null) {
			try {
				return Float.parseFloat(trim().match("[+|-]?\\d*\\.?\\d*").value());			
			} catch (NumberFormatException e) {
				logger.warn(String.format("\"%s\" toFloat", target), e);
			}
		}
		return defaultValue;
	}
    
    public int toInt() {        
        return toInt(0);
    }
    
	public int toInt(int defaultValue) {        
        if (target != null) {
            try {
                return Integer.parseInt(trim().match("[+|-]?\\d+").value());            
            } catch (NumberFormatException e) {
                logger.warn(String.format("\"%s\" toInt", target), e);
            }
        }
        return defaultValue;
    }
	
	public long toLong() {
	    return toLong(0);
	}
	
	public long toLong(long defaultValue) {
	    if (target != null) {
	        try {
                return Long.parseLong(trim().match("[+|-]?\\d+").value());            
            } catch (NumberFormatException e) {
                logger.warn(String.format("\"%s\" toLong", target), e);
            }
	    }
	    return defaultValue;
	}
	
	public XString toLowerCase() {
	    return target == null ? NULL : new XString(target.toLowerCase());
	}
	
	@Override
	public String toString() {
		return target;
	}
	
	public XString toUpperCase() {
	    return target == null ? NULL : new XString(target.toUpperCase());
	}
	
	public XString trim() {
		return target != null ? new XString(target.trim()) : NULL;
	}
	
	public String value() {
		return target;
	}

}
