/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import eu.darkcube.system.BaseMessage;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;

@Api public enum Messages implements BaseMessage {

    ERROR_INTS_ONLY("error_ints_only"),
    ERROR_EMPTY("error_empty"),
    ERROR_SWAPPED("error_swapped"),
    TOO_MANY_ENTITIES("too_many_entities"),
    SELECTOR_NOT_ALLOWED("selector_not_allowed"),
    ONLY_PLAYERS_ALLOWED("only_players_allowed"),
    TOO_MANY_PLAYERS("too_many_players"),
    UNKNOWN_COMMAND_EXCEPTION_TYPE("unknown_command_exception_type"),
    INVALID_ENTITY_NAME_OR_UUID("invalid_entity_name_or_uuid"),
    SELECTOR_TYPE_MISSING("selector_type_missing"),
    EXPECTED_END_OF_OPTIONS("expected_end_of_options"),
    EXPECTED_VALUE_FOR_OPTION("expected_value_for_option"),
    ENTITY_NOT_FOUND("entity_not_found"),
    PLAYER_NOT_FOUND("player_not_found"),
    UNKNOWN_ENTITY_OPTION("unknown_entity_option"),
    INAPPLICABLE_ENTITY_OPTION("inapplicable_entity_option"),
    NEGATIVE_DISTANCE("negative_distance"),
    NEGATIVE_LEVEL("negative_level"),
    NONPOSITIVE_LIMIT("nonpositive_limit"),
    INVALID_SORT("invalid_sort"),
    INVALID_GAME_MODE("invalid_game_mode"),
    INVALID_ENTITY_TYPE("invalid_entity_type"),
    SELECTOR_NEAREST_PLAYER("selector_nearest_player"),
    SELECTOR_ALL_PLAYERS("selector_all_players"),
    SELECTOR_RANDOM_PLAYER("selector_random_player"),
    SELECTOR_SELF("selector_self"),
    SELECTOR_ALL_ENTITIES("selector_all_entities"),
    EXPECTED_DOUBLE("expected_double"),
    EXPECTED_INT("expected_int"),
    VEC2_INCOMPLETE("vec2_incomplete"),
    POS_INCOMPLETE("pos_incomplete"),
    POS_MIXED_TYPES("pos_mixed_types"),
    ROTATION_INCOMPLETE("rotation_incomplete"),
    ANCHOR_INVALID("anchor_invalid"),
    INVALID_ENUM("invalid_enum"),
    INVALID_WORLD("invalid_world"),
    INVALID_UUID("invalid_uuid"),
    BOOLEAN_INVALID("boolean_invalid"),
    SERVICE_TASK_NOT_PRESENT("service_task_not_present"),

    ;

    public static final Map<String, Messages> MESSAGES = new HashMap<>();

    static {
        for (var message : values()) {
            MESSAGES.put(message.key(), message);
        }
    }

    private final String key;

    Messages(final String key) {
        this.key = key;
    }

    @Override public String key() {
        return key;
    }

    @Override public String toString() {
        return key();
    }

    @Api public record MessageWrapper(BaseMessage message, Object... components) implements Message {

        @Override public String getString() {
            return message.toString();
        }

        @Override public int hashCode() {
            return message.hashCode();
        }

        @Override public boolean equals(Object obj) {
            return obj instanceof MessageWrapper && Objects.equals(((MessageWrapper) obj).message, message) && Arrays.equals(components, ((MessageWrapper) obj).components);
        }
    }
}
