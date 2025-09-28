/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.sodg;

import com.yegor256.xsline.Shift;
import com.yegor256.xsline.Train;

/**
 * Train depo.
 * @since 0.0.3
 */
interface Depo {

    /**
     * Train.
     * @param name Train name
     * @return Train
     */
    Train<Shift> train(String name);
}
