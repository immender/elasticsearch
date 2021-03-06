/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

package org.elasticsearch.xpack.autoscaling.decision;

import org.elasticsearch.common.io.stream.NamedWriteable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents an autoscaling decision from a single decider
 */
public class AutoscalingDecision implements ToXContent, Writeable {

    private final AutoscalingCapacity requiredCapacity;
    private final Reason reason;

    public interface Reason extends ToXContent, NamedWriteable {
        String summary();
    }

    /**
     * Create a new decision with required capacity.
     * @param requiredCapacity required capacity or null if no decision can be made due to insufficient information.
     * @param reason details/data behind the decision
     */
    public AutoscalingDecision(AutoscalingCapacity requiredCapacity, Reason reason) {
        this.requiredCapacity = requiredCapacity;
        this.reason = reason;
    }

    public AutoscalingDecision(StreamInput in) throws IOException {
        this.requiredCapacity = in.readOptionalWriteable(AutoscalingCapacity::new);
        this.reason = in.readOptionalNamedWriteable(Reason.class);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeOptionalWriteable(requiredCapacity);
        out.writeOptionalNamedWriteable(reason);
    }

    public AutoscalingCapacity requiredCapacity() {
        return requiredCapacity;
    }

    public Reason reason() {
        return reason;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        if (requiredCapacity != null) {
            builder.field("required_capacity", requiredCapacity);
        }

        if (reason != null) {
            builder.field("reason_summary", reason.summary());
            builder.field("reason_details", reason);
        }

        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoscalingDecision that = (AutoscalingDecision) o;
        return Objects.equals(requiredCapacity, that.requiredCapacity) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredCapacity, reason);
    }
}
