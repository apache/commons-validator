/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;

/**
 * <p><b>Domain name</b> validation routines.</p>
 *
 * <p>
 * This validator provides methods for validating Internet domain names
 * and top-level domains.
 * </p>
 *
 * <p>Domain names are evaluated according
 * to the standards <a href="http://www.ietf.org/rfc/rfc1034.txt">RFC1034</a>,
 * section 3, and <a href="http://www.ietf.org/rfc/rfc1123.txt">RFC1123</a>,
 * section 2.1. No accommodation is provided for the specialized needs of
 * other applications; if the domain name has been URL-encoded, for example,
 * validation will fail even though the equivalent plaintext version of the
 * same name would have passed.
 * </p>
 *
 * <p>
 * Validation is also provided for top-level domains (TLDs) as defined and
 * maintained by the Internet Assigned Numbers Authority (IANA):
 * </p>
 *
 *   <ul>
 *     <li>{@link #isValidInfrastructureTld} - validates infrastructure TLDs
 *         (<code>.arpa</code>, etc.)</li>
 *     <li>{@link #isValidGenericTld} - validates generic TLDs
 *         (<code>.com, .org</code>, etc.)</li>
 *     <li>{@link #isValidCountryCodeTld} - validates country code TLDs
 *         (<code>.us, .uk, .cn</code>, etc.)</li>
 *   </ul>
 *
 * <p>
 * (<b>NOTE</b>: This class does not provide IP address lookup for domain names or
 * methods to ensure that a given domain name matches a specific IP; see
 * {@link java.net.InetAddress} for that functionality.)
 * </p>
 *
 * @version $Revision$
 * @since Validator 1.4
 */
public class DomainValidator implements Serializable {

    private static final long serialVersionUID = -4407125112880174009L;

    // Regular expression strings for hostnames (derived from RFC2396 and RFC 1123)

    // RFC2396: domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*";

    // RFC2396 toplabel = alpha | alpha *( alphanum | "-" ) alphanum
    private static final String TOP_LABEL_REGEX = "\\p{Alpha}|(?:\\p{Alpha}(?:[\\p{Alnum}-])*\\p{Alnum})";

    private static final String DOMAIN_NAME_REGEX =
            "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX + ")$";

    private final boolean allowLocal;

    /**
     * Singleton instance of this validator, which
     *  doesn't consider local addresses as valid.
     */
    private static final DomainValidator DOMAIN_VALIDATOR = new DomainValidator(false);

    /**
     * Singleton instance of this validator, which does
     *  consider local addresses valid.
     */
    private static final DomainValidator DOMAIN_VALIDATOR_WITH_LOCAL = new DomainValidator(true);

    /**
     * RegexValidator for matching domains.
     */
    private final RegexValidator domainRegex =
            new RegexValidator(DOMAIN_NAME_REGEX);
    /**
     * RegexValidator for matching the a local hostname
     */
    private final RegexValidator hostnameRegex =
            new RegexValidator(DOMAIN_LABEL_REGEX);

    /**
     * Returns the singleton instance of this validator. It
     *  will not consider local addresses as valid.
     * @return the singleton instance of this validator
     */
    public static DomainValidator getInstance() {
        return DOMAIN_VALIDATOR;
    }

    /**
     * Returns the singleton instance of this validator,
     *  with local validation as required.
     * @param allowLocal Should local addresses be considered valid?
     * @return the singleton instance of this validator
     */
    public static DomainValidator getInstance(boolean allowLocal) {
       if(allowLocal) {
          return DOMAIN_VALIDATOR_WITH_LOCAL;
       }
       return DOMAIN_VALIDATOR;
    }

    /** Private constructor. */
    private DomainValidator(boolean allowLocal) {
       this.allowLocal = allowLocal;
    }

    /**
     * Returns true if the specified <code>String</code> parses
     * as a valid domain name with a recognized top-level domain.
     * The parsing is case-insensitive.
     * @param domain the parameter to check for domain name syntax
     * @return true if the parameter is a valid domain name
     */
    public boolean isValid(String domain) {
        String[] groups = domainRegex.match(domain);
        if (groups != null && groups.length > 0) {
            return isValidTld(groups[0]);
        }
        return allowLocal && hostnameRegex.isValid(domain);
    }

    // package protected for unit test access 
    final boolean isValidDomainSyntax(String domain) {
        String[] groups = domainRegex.match(domain);
        return (groups != null && groups.length > 0);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined top-level domain. Leading dots are ignored if present.
     * The search is case-insensitive.
     * @param tld the parameter to check for TLD status
     * @return true if the parameter is a TLD
     */
    public boolean isValidTld(String tld) {
        if(allowLocal && isValidLocalTld(tld)) {
           return true;
        }
        return isValidInfrastructureTld(tld)
                || isValidGenericTld(tld)
                || isValidCountryCodeTld(tld);
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined infrastructure top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param iTld the parameter to check for infrastructure TLD status
     * @return true if the parameter is an infrastructure TLD
     */
    public boolean isValidInfrastructureTld(String iTld) {
        return Arrays.binarySearch(INFRASTRUCTURE_TLDS, (chompLeadingDot(iTld.toLowerCase(Locale.ENGLISH)))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined generic top-level domain. Leading dots are ignored
     * if present. The search is case-insensitive.
     * @param gTld the parameter to check for generic TLD status
     * @return true if the parameter is a generic TLD
     */
    public boolean isValidGenericTld(String gTld) {
        return Arrays.binarySearch(GENERIC_TLDS, chompLeadingDot(gTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * IANA-defined country code top-level domain. Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param ccTld the parameter to check for country code TLD status
     * @return true if the parameter is a country code TLD
     */
    public boolean isValidCountryCodeTld(String ccTld) {
        return Arrays.binarySearch(COUNTRY_CODE_TLDS, chompLeadingDot(ccTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    /**
     * Returns true if the specified <code>String</code> matches any
     * widely used "local" domains (localhost or localdomain). Leading dots are
     * ignored if present. The search is case-insensitive.
     * @param iTld the parameter to check for local TLD status
     * @return true if the parameter is an local TLD
     */
    public boolean isValidLocalTld(String iTld) {
        return Arrays.binarySearch(LOCAL_TLDS, chompLeadingDot(iTld.toLowerCase(Locale.ENGLISH))) >= 0;
    }

    private String chompLeadingDot(String str) {
        if (str.startsWith(".")) {
            return str.substring(1);
        }
        return str;
    }

    // ---------------------------------------------
    // ----- TLDs defined by IANA
    // ----- Authoritative and comprehensive list at:
    // ----- http://data.iana.org/TLD/tlds-alpha-by-domain.txt

    // Note that the above list is in UPPER case.
    // The code currently converts strings to lower case (as per the tables below)

    // IANA also provide an HTML list at http://www.iana.org/domains/root/db
    // Note that this contains several country code entries which are NOT in
    // the text file. These all have the "Not assigned" in the "Sponsoring Organisation" column
    // For example (as of 2015-01-02):
    // .bl  country-code    Not assigned
    // .um  country-code    Not assigned

    private static final String[] INFRASTRUCTURE_TLDS = new String[] {
        "arpa",               // internet infrastructure
        "root"                // diagnostic marker for non-truncated root zone
    };

    private static final String[] GENERIC_TLDS = new String[] {
        "abogado",
        "academy",
        "accountants",
        "active",
        "actor",
        "adult",
        "aero",
        "agency",
        "airforce",
        "allfinanz",
        "alsace",
        "amsterdam",
        "android",
        "aquarelle",
        "archi",
        "army",
        "arpa",
        "asia",
        "associates",
        "attorney",
        "auction",
        "audio",
        "autos",
        "axa",
        "band",
        "bar",
        "bargains",
        "bayern",
        "beer",
        "berlin",
        "best",
        "bid",
        "bike",
        "bio",
        "biz",
        "black",
        "blackfriday",
        "bloomberg",
        "blue",
        "bmw",
        "bnpparibas",
        "boo",
        "boutique",
        "brussels",
        "budapest",
        "build",
        "builders",
        "business",
        "buzz",
        "bzh",
        "cab",
        "cal",
        "camera",
        "camp",
        "cancerresearch",
        "capetown",
        "capital",
        "caravan",
        "cards",
        "care",
        "career",
        "careers",
        "cartier",
        "casa",
        "cash",
        "cat",
        "catering",
        "center",
        "ceo",
        "cern",
        "channel",
        "cheap",
        "christmas",
        "chrome",
        "church",
        "citic",
        "city",
        "claims",
        "cleaning",
        "click",
        "clinic",
        "clothing",
        "club",
        "coach",
        "codes",
        "coffee",
        "college",
        "cologne",
        "com",
        "community",
        "company",
        "computer",
        "condos",
        "construction",
        "consulting",
        "contractors",
        "cooking",
        "cool",
        "coop",
        "country",
        "credit",
        "creditcard",
        "cricket",
        "crs",
        "cruises",
        "cuisinella",
        "cymru",
        "dad",
        "dance",
        "dating",
        "day",
        "deals",
        "degree",
        "delivery",
        "democrat",
        "dental",
        "dentist",
        "desi",
        "dev",
        "diamonds",
        "diet",
        "digital",
        "direct",
        "directory",
        "discount",
        "dnp",
        "docs",
        "domains",
        "doosan",
        "durban",
        "dvag",
        "eat",
        "edu",
        "education",
        "email",
        "emerck",
        "energy",
        "engineer",
        "engineering",
        "enterprises",
        "equipment",
        "esq",
        "estate",
        "eurovision",
        "eus",
        "events",
        "everbank",
        "exchange",
        "expert",
        "exposed",
        "fail",
        "farm",
        "fashion",
        "feedback",
        "finance",
        "financial",
        "firmdale",
        "fish",
        "fishing",
        "fitness",
        "flights",
        "florist",
        "flowers",
        "flsmidth",
        "fly",
        "foo",
        "forsale",
        "foundation",
        "frl",
        "frogans",
        "fund",
        "furniture",
        "futbol",
        "gal",
        "gallery",
        "garden",
        "gbiz",
        "gent",
        "ggee",
        "gift",
        "gifts",
        "gives",
        "glass",
        "gle",
        "global",
        "globo",
        "gmail",
        "gmo",
        "gmx",
        "google",
        "gop",
        "gov",
        "graphics",
        "gratis",
        "green",
        "gripe",
        "guide",
        "guitars",
        "guru",
        "hamburg",
        "haus",
        "healthcare",
        "help",
        "here",
        "hiphop",
        "hiv",
        "holdings",
        "holiday",
        "homes",
        "horse",
        "host",
        "hosting",
        "house",
        "how",
        "ibm",
        "immo",
        "immobilien",
        "industries",
        "info",
        "ing",
        "ink",
        "institute",
        "insure",
        "int",
        "international",
        "investments",
        "irish",
        "iwc",
        "jetzt",
        "jobs",
        "joburg",
        "juegos",
        "kaufen",
        "kim",
        "kitchen",
        "kiwi",
        "koeln",
        "krd",
        "kred",
        "lacaixa",
        "land",
        "latrobe",
        "lawyer",
        "lds",
        "lease",
        "legal",
        "lgbt",
        "lidl",
        "life",
        "lighting",
        "limited",
        "limo",
        "link",
        "loans",
        "london",
        "lotto",
        "ltda",
        "luxe",
        "luxury",
        "madrid",
        "maison",
        "management",
        "mango",
        "market",
        "marketing",
        "media",
        "meet",
        "melbourne",
        "meme",
        "memorial",
        "menu",
        "miami",
        "mil",
        "mini",
        "mobi",
        "moda",
        "moe",
        "monash",
        "money",
        "mormon",
        "mortgage",
        "moscow",
        "motorcycles",
        "mov",
        "museum",
        "nagoya",
        "name",
        "navy",
        "net",
        "network",
        "neustar",
        "new",
        "nexus",
        "ngo",
        "nhk",
        "ninja",
        "nra",
        "nrw",
        "nyc",
        "okinawa",
        "ong",
        "onl",
        "ooo",
        "org",
        "organic",
        "osaka",
        "otsuka",
        "ovh",
        "paris",
        "partners",
        "parts",
        "party",
        "pharmacy",
        "photo",
        "photography",
        "photos",
        "physio",
        "pics",
        "pictures",
        "pink",
        "pizza",
        "place",
        "plumbing",
        "pohl",
        "poker",
        "porn",
        "post",
        "praxi",
        "press",
        "pro",
        "prod",
        "productions",
        "prof",
        "properties",
        "property",
        "pub",
        "qpon",
        "quebec",
        "realtor",
        "recipes",
        "red",
        "rehab",
        "reise",
        "reisen",
        "reit",
        "ren",
        "rentals",
        "repair",
        "report",
        "republican",
        "rest",
        "restaurant",
        "reviews",
        "rich",
        "rio",
        "rip",
        "rocks",
        "rodeo",
        "rsvp",
        "ruhr",
        "ryukyu",
        "saarland",
        "sale",
        "samsung",
        "sarl",
        "sca",
        "scb",
        "schmidt",
        "schule",
        "schwarz",
        "science",
        "scot",
        "services",
        "sew",
        "sexy",
        "shiksha",
        "shoes",
        "shriram",
        "singles",
        "sky",
        "social",
        "software",
        "sohu",
        "solar",
        "solutions",
        "soy",
        "space",
        "spiegel",
        "supplies",
        "supply",
        "support",
        "surf",
        "surgery",
        "suzuki",
        "sydney",
        "systems",
        "taipei",
        "tatar",
        "tattoo",
        "tax",
        "technology",
        "tel",
        "tienda",
        "tips",
        "tires",
        "tirol",
        "today",
        "tokyo",
        "tools",
        "top",
        "town",
        "toys",
        "trade",
        "training",
        "travel",
        "trust",
        "tui",
        "university",
        "uno",
        "uol",
        "vacations",
        "vegas",
        "ventures",
//        "vermögensberater", // xn--vermgensberater-ctb Deutsche Vermögensberatung Aktiengesellschaft DVAG
//        "vermögensberatung", // xn--vermgensberatung-pwb Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "versicherung",
        "vet",
        "viajes",
        "video",
        "villas",
        "vision",
        "vlaanderen",
        "vodka",
        "vote",
        "voting",
        "voto",
        "voyage",
        "wales",
        "wang",
        "watch",
        "webcam",
        "website",
        "wed",
        "wedding",
        "whoswho",
        "wien",
        "wiki",
        "williamhill",
        "wme",
        "work",
        "works",
        "world",
        "wtc",
        "wtf",
        "xn--1qqw23a", // 佛山 Guangzhou YU Wei Information Technology Co., Ltd.
        "xn--3bst00m", // 集团 Eagle Horizon Limited
        "xn--3ds443g", // 在线 TLD REGISTRY LIMITED
        "xn--45q11c", // 八卦 Zodiac Scorpio Limited
        "xn--4gbrim", // موقع Suhub Electronic Establishment
        "xn--55qw42g", // 公益 China Organizational Name Administration Center
        "xn--55qx5d", // 公司 Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
        "xn--6frz82g", // 移动 Afilias Limited
        "xn--6qq986b3xl", // 我爱你 Tycoon Treasure Limited
        "xn--80adxhks", // москва Foundation for Assistance for Internet Technologies and Infrastructure Development (FAITID)
        "xn--80asehdb", // онлайн CORE Association
        "xn--80aswg", // сайт CORE Association
        "xn--c1avg", // орг Public Interest Registry
        "xn--cg4bki", // 삼성 SAMSUNG SDS CO., LTD
        "xn--czr694b", // 商标 HU YI GLOBAL INFORMATION RESOURCES(HOLDING) COMPANY.HONGKONG LIMITED
        "xn--czrs0t", // 商店 Wild Island, LLC
        "xn--czru2d", // 商城 Zodiac Aquarius Limited
        "xn--d1acj3b", // дети The Foundation for Network Initiatives “The Smart Internet”
        "xn--fiq228c5hs", // 中文网 TLD REGISTRY LIMITED
        "xn--fiq64b", // 中信 CITIC Group Corporation
        "xn--flw351e", // 谷歌 Charleston Road Registry Inc.
        "xn--hxt814e", // 网店 Zodiac Libra Limited
        "xn--i1b6b1a6a2e", // संगठन Public Interest Registry
        "xn--io0a7i", // 网络 Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
        "xn--kput3i", // 手机 Beijing RITT-Net Technology Development Co., Ltd
        "xn--mgbab2bd", // بازار CORE Association
        "xn--ngbc5azd", // شبكة International Domain Registry Pty. Ltd.
        "xn--nqv7f", // 机构 Public Interest Registry
        "xn--nqv7fs00ema", // 组织机构 Public Interest Registry
        "xn--p1acf", // рус Rusnames Limited
        "xn--q9jyb4c", // みんな Charleston Road Registry Inc.
        "xn--qcka1pmc", // グーグル Charleston Road Registry Inc.
        "xn--rhqv96g", // 世界 Stable Tone Limited
        "xn--ses554g", // 网址 HU YI GLOBAL INFORMATION RESOURCES (HOLDING) COMPANY. HONGKONG LIMITED
        "xn--unup4y", // 游戏 Spring Fields, LLC
        "xn--vermgensberater-ctb", // vermögensberater Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "xn--vermgensberatung-pwb", // vermögensberatung Deutsche Vermögensberatung Aktiengesellschaft DVAG
        "xn--vhquv", // 企业 Dash McCook, LLC
        "xn--xhq521b", // 广东 Guangzhou YU Wei Information Technology Co., Ltd.
        "xn--zfr164b", // 政务 China Organizational Name Administration Center
        "xxx",
        "xyz",
        "yachts",
        "yandex",
        "yoga",
        "yokohama",
        "youtube",
        "zip",
        "zone",
        "zuerich",
//        "дети", // xn--d1acj3b The Foundation for Network Initiatives “The Smart Internet”
//        "москва", // xn--80adxhks Foundation for Assistance for Internet Technologies and Infrastructure Development (FAITID)
//        "онлайн", // xn--80asehdb CORE Association
//        "орг", // xn--c1avg Public Interest Registry
//        "рус", // xn--p1acf Rusnames Limited
//        "сайт", // xn--80aswg CORE Association
//        "بازار", // xn--mgbab2bd CORE Association
//        "شبكة", // xn--ngbc5azd International Domain Registry Pty. Ltd.
//        "موقع", // xn--4gbrim Suhub Electronic Establishment
//        "संगठन", // xn--i1b6b1a6a2e Public Interest Registry
//        "みんな", // xn--q9jyb4c Charleston Road Registry Inc.
//        "グーグル", // xn--qcka1pmc Charleston Road Registry Inc.
//        "世界", // xn--rhqv96g Stable Tone Limited
//        "中信", // xn--fiq64b CITIC Group Corporation
//        "中文网", // xn--fiq228c5hs TLD REGISTRY LIMITED
//        "企业", // xn--vhquv Dash McCook, LLC
//        "佛山", // xn--1qqw23a Guangzhou YU Wei Information Technology Co., Ltd.
//        "八卦", // xn--45q11c Zodiac Scorpio Limited
//        "公司", // xn--55qx5d Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
//        "公益", // xn--55qw42g China Organizational Name Administration Center
//        "商城", // xn--czru2d Zodiac Aquarius Limited
//        "商店", // xn--czrs0t Wild Island, LLC
//        "商标", // xn--czr694b HU YI GLOBAL INFORMATION RESOURCES(HOLDING) COMPANY.HONGKONG LIMITED
//        "在线", // xn--3ds443g TLD REGISTRY LIMITED
//        "广东", // xn--xhq521b Guangzhou YU Wei Information Technology Co., Ltd.
//        "我爱你", // xn--6qq986b3xl Tycoon Treasure Limited
//        "手机", // xn--kput3i Beijing RITT-Net Technology Development Co., Ltd
//        "政务", // xn--zfr164b China Organizational Name Administration Center
//        "机构", // xn--nqv7f Public Interest Registry
//        "游戏", // xn--unup4y Spring Fields, LLC
//        "移动", // xn--6frz82g Afilias Limited
//        "组织机构", // xn--nqv7fs00ema Public Interest Registry
//        "网址", // xn--ses554g HU YI GLOBAL INFORMATION RESOURCES (HOLDING) COMPANY. HONGKONG LIMITED
//        "网店", // xn--hxt814e Zodiac Libra Limited
//        "网络", // xn--io0a7i Computer Network Information Center of Chinese Academy of Sciences （China Internet Network Information Center）
//        "谷歌", // xn--flw351e Charleston Road Registry Inc.
//        "集团", // xn--3bst00m Eagle Horizon Limited
//        "삼성", // xn--cg4bki SAMSUNG SDS CO., LTD
   };

    private static final String[] COUNTRY_CODE_TLDS = new String[] {
        "ac",                 // Ascension Island
        "ad",                 // Andorra
        "ae",                 // United Arab Emirates
        "af",                 // Afghanistan
        "ag",                 // Antigua and Barbuda
        "ai",                 // Anguilla
        "al",                 // Albania
        "am",                 // Armenia
        "an",                 // Netherlands Antilles
        "ao",                 // Angola
        "aq",                 // Antarctica
        "ar",                 // Argentina
        "as",                 // American Samoa
        "at",                 // Austria
        "au",                 // Australia (includes Ashmore and Cartier Islands and Coral Sea Islands)
        "aw",                 // Aruba
        "ax",                 // Åland
        "az",                 // Azerbaijan
        "ba",                 // Bosnia and Herzegovina
        "bb",                 // Barbados
        "bd",                 // Bangladesh
        "be",                 // Belgium
        "bf",                 // Burkina Faso
        "bg",                 // Bulgaria
        "bh",                 // Bahrain
        "bi",                 // Burundi
        "bj",                 // Benin
        "bm",                 // Bermuda
        "bn",                 // Brunei Darussalam
        "bo",                 // Bolivia
        "br",                 // Brazil
        "bs",                 // Bahamas
        "bt",                 // Bhutan
        "bv",                 // Bouvet Island
        "bw",                 // Botswana
        "by",                 // Belarus
        "bz",                 // Belize
        "ca",                 // Canada
        "cc",                 // Cocos (Keeling) Islands
        "cd",                 // Democratic Republic of the Congo (formerly Zaire)
        "cf",                 // Central African Republic
        "cg",                 // Republic of the Congo
        "ch",                 // Switzerland
        "ci",                 // Côte d'Ivoire
        "ck",                 // Cook Islands
        "cl",                 // Chile
        "cm",                 // Cameroon
        "cn",                 // China, mainland
        "co",                 // Colombia
        "cr",                 // Costa Rica
        "cu",                 // Cuba
        "cv",                 // Cape Verde
        "cw",                 // Curaçao
        "cx",                 // Christmas Island
        "cy",                 // Cyprus
        "cz",                 // Czech Republic
        "de",                 // Germany
        "dj",                 // Djibouti
        "dk",                 // Denmark
        "dm",                 // Dominica
        "do",                 // Dominican Republic
        "dz",                 // Algeria
        "ec",                 // Ecuador
        "ee",                 // Estonia
        "eg",                 // Egypt
        "er",                 // Eritrea
        "es",                 // Spain
        "et",                 // Ethiopia
        "eu",                 // European Union
        "fi",                 // Finland
        "fj",                 // Fiji
        "fk",                 // Falkland Islands
        "fm",                 // Federated States of Micronesia
        "fo",                 // Faroe Islands
        "fr",                 // France
        "ga",                 // Gabon
        "gb",                 // Great Britain (United Kingdom)
        "gd",                 // Grenada
        "ge",                 // Georgia
        "gf",                 // French Guiana
        "gg",                 // Guernsey
        "gh",                 // Ghana
        "gi",                 // Gibraltar
        "gl",                 // Greenland
        "gm",                 // The Gambia
        "gn",                 // Guinea
        "gp",                 // Guadeloupe
        "gq",                 // Equatorial Guinea
        "gr",                 // Greece
        "gs",                 // South Georgia and the South Sandwich Islands
        "gt",                 // Guatemala
        "gu",                 // Guam
        "gw",                 // Guinea-Bissau
        "gy",                 // Guyana
        "hk",                 // Hong Kong
        "hm",                 // Heard Island and McDonald Islands
        "hn",                 // Honduras
        "hr",                 // Croatia (Hrvatska)
        "ht",                 // Haiti
        "hu",                 // Hungary
        "id",                 // Indonesia
        "ie",                 // Ireland (Éire)
        "il",                 // Israel
        "im",                 // Isle of Man
        "in",                 // India
        "io",                 // British Indian Ocean Territory
        "iq",                 // Iraq
        "ir",                 // Iran
        "is",                 // Iceland
        "it",                 // Italy
        "je",                 // Jersey
        "jm",                 // Jamaica
        "jo",                 // Jordan
        "jp",                 // Japan
        "ke",                 // Kenya
        "kg",                 // Kyrgyzstan
        "kh",                 // Cambodia (Khmer)
        "ki",                 // Kiribati
        "km",                 // Comoros
        "kn",                 // Saint Kitts and Nevis
        "kp",                 // North Korea
        "kr",                 // South Korea
        "kw",                 // Kuwait
        "ky",                 // Cayman Islands
        "kz",                 // Kazakhstan
        "la",                 // Laos (currently being marketed as the official domain for Los Angeles)
        "lb",                 // Lebanon
        "lc",                 // Saint Lucia
        "li",                 // Liechtenstein
        "lk",                 // Sri Lanka
        "lr",                 // Liberia
        "ls",                 // Lesotho
        "lt",                 // Lithuania
        "lu",                 // Luxembourg
        "lv",                 // Latvia
        "ly",                 // Libya
        "ma",                 // Morocco
        "mc",                 // Monaco
        "md",                 // Moldova
        "me",                 // Montenegro
        "mg",                 // Madagascar
        "mh",                 // Marshall Islands
        "mk",                 // Republic of Macedonia
        "ml",                 // Mali
        "mm",                 // Myanmar
        "mn",                 // Mongolia
        "mo",                 // Macau
        "mp",                 // Northern Mariana Islands
        "mq",                 // Martinique
        "mr",                 // Mauritania
        "ms",                 // Montserrat
        "mt",                 // Malta
        "mu",                 // Mauritius
        "mv",                 // Maldives
        "mw",                 // Malawi
        "mx",                 // Mexico
        "my",                 // Malaysia
        "mz",                 // Mozambique
        "na",                 // Namibia
        "nc",                 // New Caledonia
        "ne",                 // Niger
        "nf",                 // Norfolk Island
        "ng",                 // Nigeria
        "ni",                 // Nicaragua
        "nl",                 // Netherlands
        "no",                 // Norway
        "np",                 // Nepal
        "nr",                 // Nauru
        "nu",                 // Niue
        "nz",                 // New Zealand
        "om",                 // Oman
        "pa",                 // Panama
        "pe",                 // Peru
        "pf",                 // French Polynesia With Clipperton Island
        "pg",                 // Papua New Guinea
        "ph",                 // Philippines
        "pk",                 // Pakistan
        "pl",                 // Poland
        "pm",                 // Saint-Pierre and Miquelon
        "pn",                 // Pitcairn Islands
        "pr",                 // Puerto Rico
        "ps",                 // Palestinian territories (PA-controlled West Bank and Gaza Strip)
        "pt",                 // Portugal
        "pw",                 // Palau
        "py",                 // Paraguay
        "qa",                 // Qatar
        "re",                 // Réunion
        "ro",                 // Romania
        "rs",                 // Serbia
        "ru",                 // Russia
        "rw",                 // Rwanda
        "sa",                 // Saudi Arabia
        "sb",                 // Solomon Islands
        "sc",                 // Seychelles
        "sd",                 // Sudan
        "se",                 // Sweden
        "sg",                 // Singapore
        "sh",                 // Saint Helena
        "si",                 // Slovenia
        "sj",                 // Svalbard and Jan Mayen Islands Not in use (Norwegian dependencies; see .no)
        "sk",                 // Slovakia
        "sl",                 // Sierra Leone
        "sm",                 // San Marino
        "sn",                 // Senegal
        "so",                 // Somalia
        "sr",                 // Suriname
        "st",                 // São Tomé and Príncipe
        "su",                 // Soviet Union (deprecated)
        "sv",                 // El Salvador
        "sx",                 // Sint Maarten
        "sy",                 // Syria
        "sz",                 // Swaziland
        "tc",                 // Turks and Caicos Islands
        "td",                 // Chad
        "tf",                 // French Southern and Antarctic Lands
        "tg",                 // Togo
        "th",                 // Thailand
        "tj",                 // Tajikistan
        "tk",                 // Tokelau
        "tl",                 // East Timor (deprecated old code)
        "tm",                 // Turkmenistan
        "tn",                 // Tunisia
        "to",                 // Tonga
        "tp",                 // East Timor
        "tr",                 // Turkey
        "tt",                 // Trinidad and Tobago
        "tv",                 // Tuvalu
        "tw",                 // Taiwan, Republic of China
        "tz",                 // Tanzania
        "ua",                 // Ukraine
        "ug",                 // Uganda
        "uk",                 // United Kingdom
        "um",                 // United States Minor Outlying Islands
                              // TODO um is not in the IANA text file, it is in the HTML file
        "us",                 // United States of America
        "uy",                 // Uruguay
        "uz",                 // Uzbekistan
        "va",                 // Vatican City State
        "vc",                 // Saint Vincent and the Grenadines
        "ve",                 // Venezuela
        "vg",                 // British Virgin Islands
        "vi",                 // U.S. Virgin Islands
        "vn",                 // Vietnam
        "vu",                 // Vanuatu
        "wf",                 // Wallis and Futuna
        "ws",                 // Samoa (formerly Western Samoa)
        "xn--3e0b707e", // 한국 KISA (Korea Internet &amp; Security Agency)
        "xn--45brj9c", // ভারত National Internet Exchange of India
        "xn--80ao21a", // қаз Association of IT Companies of Kazakhstan
        "xn--90a3ac", // срб Serbian National Internet Domain Registry (RNIDS)
        "xn--clchc0ea0b2g2a9gcd", // சிங்கப்பூர் Singapore Network Information Centre (SGNIC) Pte Ltd
        "xn--d1alf", // мкд Macedonian Academic Research Network Skopje
        "xn--fiqs8s", // 中国 China Internet Network Information Center
        "xn--fiqz9s", // 中國 China Internet Network Information Center
        "xn--fpcrj9c3d", // భారత్ National Internet Exchange of India
        "xn--fzc2c9e2c", // ලංකා LK Domain Registry
        "xn--gecrj9c", // ભારત National Internet Exchange of India
        "xn--h2brj9c", // भारत National Internet Exchange of India
        "xn--j1amh", // укр Ukrainian Network Information Centre (UANIC), Inc.
        "xn--j6w193g", // 香港 Hong Kong Internet Registration Corporation Ltd.
        "xn--kprw13d", // 台湾 Taiwan Network Information Center (TWNIC)
        "xn--kpry57d", // 台灣 Taiwan Network Information Center (TWNIC)
        "xn--l1acc", // мон Datacom Co.,Ltd
        "xn--lgbbat1ad8j", // الجزائر CERIST
        "xn--mgb9awbf", // عمان Telecommunications Regulatory Authority (TRA)
        "xn--mgba3a4f16a", // ایران Institute for Research in Fundamental Sciences (IPM)
        "xn--mgbaam7a8h", // امارات Telecommunications Regulatory Authority (TRA)
        "xn--mgbayh7gpa", // الاردن National Information Technology Center (NITC)
        "xn--mgbbh1a71e", // بھارت National Internet Exchange of India
        "xn--mgbc0a9azcg", // المغرب Agence Nationale de Réglementation des Télécommunications (ANRT)
        "xn--mgberp4a5d4ar", // السعودية Communications and Information Technology Commission
        "xn--mgbx4cd0ab", // مليسيا MYNIC Berhad
        "xn--node", // გე Information Technologies Development Center (ITDC)
        "xn--o3cw4h", // ไทย Thai Network Information Center Foundation
        "xn--ogbpf8fl", // سورية National Agency for Network Services (NANS)
        "xn--p1ai", // рф Coordination Center for TLD RU
        "xn--pgbs0dh", // تونس Agence Tunisienne d&#39;Internet
        "xn--s9brj9c", // ਭਾਰਤ National Internet Exchange of India
        "xn--wgbh1c", // مصر National Telecommunication Regulatory Authority - NTRA
        "xn--wgbl6a", // قطر Communications Regulatory Authority
        "xn--xkc2al3hye2a", // இலங்கை LK Domain Registry
        "xn--xkc2dl3a5ee0h", // இந்தியா National Internet Exchange of India
        "xn--yfro4i67o", // 新加坡 Singapore Network Information Centre (SGNIC) Pte Ltd
        "xn--ygbi2ammx", // فلسطين Ministry of Telecom &amp; Information Technology (MTIT)
        "ye",                 // Yemen
        "yt",                 // Mayotte
        "yu",                 // Serbia and Montenegro (originally Yugoslavia)
                              // TODO yu is not in the IANA text file, nor it is in the HTML file
                              // It looks like it has been removed from use
        "za",                 // South Africa
        "zm",                 // Zambia
        "zw",                 // Zimbabwe
//        "мкд", // xn--d1alf Macedonian Academic Research Network Skopje
//        "мон", // xn--l1acc Datacom Co.,Ltd
//        "рф", // xn--p1ai Coordination Center for TLD RU
//        "срб", // xn--90a3ac Serbian National Internet Domain Registry (RNIDS)
//        "укр", // xn--j1amh Ukrainian Network Information Centre (UANIC), Inc.
//        "қаз", // xn--80ao21a Association of IT Companies of Kazakhstan
//        "الاردن", // xn--mgbayh7gpa National Information Technology Center (NITC)
//        "الجزائر", // xn--lgbbat1ad8j CERIST
//        "السعودية", // xn--mgberp4a5d4ar Communications and Information Technology Commission
//        "المغرب", // xn--mgbc0a9azcg Agence Nationale de Réglementation des Télécommunications (ANRT)
//        "امارات", // xn--mgbaam7a8h Telecommunications Regulatory Authority (TRA)
//        "ایران", // xn--mgba3a4f16a Institute for Research in Fundamental Sciences (IPM)
//        "بھارت", // xn--mgbbh1a71e National Internet Exchange of India
//        "تونس", // xn--pgbs0dh Agence Tunisienne d&#39;Internet
//        "سورية", // xn--ogbpf8fl National Agency for Network Services (NANS)
//        "عمان", // xn--mgb9awbf Telecommunications Regulatory Authority (TRA)
//        "فلسطين", // xn--ygbi2ammx Ministry of Telecom &amp; Information Technology (MTIT)
//        "قطر", // xn--wgbl6a Communications Regulatory Authority
//        "مصر", // xn--wgbh1c National Telecommunication Regulatory Authority - NTRA
//        "مليسيا", // xn--mgbx4cd0ab MYNIC Berhad
//        "भारत", // xn--h2brj9c National Internet Exchange of India
//        "ভারত", // xn--45brj9c National Internet Exchange of India
//        "ਭਾਰਤ", // xn--s9brj9c National Internet Exchange of India
//        "ભારત", // xn--gecrj9c National Internet Exchange of India
//        "இந்தியா", // xn--xkc2dl3a5ee0h National Internet Exchange of India
//        "இலங்கை", // xn--xkc2al3hye2a LK Domain Registry
//        "சிங்கப்பூர்", // xn--clchc0ea0b2g2a9gcd Singapore Network Information Centre (SGNIC) Pte Ltd
//        "భారత్", // xn--fpcrj9c3d National Internet Exchange of India
//        "ලංකා", // xn--fzc2c9e2c LK Domain Registry
//        "ไทย", // xn--o3cw4h Thai Network Information Center Foundation
//        "გე", // xn--node Information Technologies Development Center (ITDC)
//        "中国", // xn--fiqs8s China Internet Network Information Center
//        "中國", // xn--fiqz9s China Internet Network Information Center
//        "台湾", // xn--kprw13d Taiwan Network Information Center (TWNIC)
//        "台灣", // xn--kpry57d Taiwan Network Information Center (TWNIC)
//        "新加坡", // xn--yfro4i67o Singapore Network Information Centre (SGNIC) Pte Ltd
//        "香港", // xn--j6w193g Hong Kong Internet Registration Corporation Ltd.
//        "한국", // xn--3e0b707e KISA (Korea Internet &amp; Security Agency)
    };

    private static final String[] LOCAL_TLDS = new String[] {
       "localdomain",         // Also widely used as localhost.localdomain
       "localhost",           // RFC2606 defined
   };

    static {
        Arrays.sort(INFRASTRUCTURE_TLDS);
        Arrays.sort(COUNTRY_CODE_TLDS);
        Arrays.sort(GENERIC_TLDS);
        Arrays.sort(LOCAL_TLDS);
    }
}
